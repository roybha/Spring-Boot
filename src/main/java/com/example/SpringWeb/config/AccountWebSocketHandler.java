package com.example.SpringWeb.config;

import com.example.SpringWeb.DTO.AccountRequest;
import com.example.SpringWeb.DTO.DepositRequest;
import com.example.SpringWeb.DTO.TransferRequest;
import com.example.SpringWeb.DTO.WithdrawRequest;
import com.example.SpringWeb.facade.AccountFacade;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.AccountOperation;
import com.example.SpringWeb.model.Currency;
import com.example.SpringWeb.service.AccountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Optional;

@Component
public class AccountWebSocketHandler extends TextWebSocketHandler {
    boolean errorIndicator;
    private final AccountService accountService;
    private final AccountFacade accountFacade;
    private final AppLogger appLogger;
    private final ObjectMapper objectMapper;
    public AccountWebSocketHandler(AccountService accountService, AccountFacade accountFacade,AppLogger appLogger, ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.accountFacade = accountFacade;
        this.objectMapper = objectMapper;
        this.appLogger = appLogger;
    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        appLogger.logInfo("З'єднання WebSocket встановлено з клієнтом: " + session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        JsonNode messageNode = objectMapper.readTree(message.getPayload());

        String responseMessage = "";
        switch (AccountOperation.getFromName(messageNode.get("operation").asText())) {
            case CREATE:
                responseMessage = createAccount(new AccountRequest(
                        messageNode.get("currency").asText(),
                        messageNode.get("balance").asDouble(),
                        messageNode.get("customerId").asLong()));
                break;
            case DELETE:
                responseMessage = deleteAccount(new AccountRequest(messageNode.get("accountNumber").asText()));
                break;
            case DEPOSIT:
                responseMessage = deposit(new DepositRequest(
                        messageNode.get("accountNumber").asText(),
                        messageNode.get("amount").asDouble()
                ));
                break;
            case TRANSFER:
                responseMessage = transfer(new TransferRequest(
                        messageNode.get("fromAccount").asText(),
                        messageNode.get("toAccount").asText(),
                        messageNode.get("amount").asDouble(),
                        messageNode.get("currency").asText()
                ));
                break;
            case WITHDRAW:
                responseMessage = withdraw(new WithdrawRequest(
                        messageNode.get("accountNumber").asText(),
                        messageNode.get("amount").asDouble()
                ));
                break;
            default:
                responseMessage = "Невідома операція";
                break;
        }
        String status = errorIndicator ? "error" : "success";
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(new WebSocketResponse(status, responseMessage))));
    }
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception){
        appLogger.logError("Помилка транспорту WebSocket: " + exception.getMessage(), exception);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        appLogger.logInfo("З'єднання WebSocket закрите з клієнтом: " + session.getId());
    }

    private String createAccount(AccountRequest accountRequest) {
        errorIndicator = false;
        try {
            String futureNumber = AccountService.generateAccountNumber();
            while (accountService.findByAccountNumber(futureNumber).isPresent()) {
                futureNumber = AccountService.generateAccountNumber();
            }
            accountRequest.setAccountNumber(futureNumber);
            if (accountService.save(accountFacade.getAccountByAccountRequest(accountRequest))) {
                appLogger.logInfo("Збереження нового акаунта");
                return "Рахунок створено успішно";
            }
            throw new Exception("Помилка при створенні рахунку");
        } catch (Exception e) {
            appLogger.logError(e.getMessage(), e);
            errorIndicator = true;
            return "Помилка при створенні рахунку";
        }
    }

    private String deleteAccount(AccountRequest accountRequest) {
        Optional<Account> account = accountService.findByAccountNumber(accountRequest.getAccountNumber());
        errorIndicator = false;
        if (account.isPresent()) {
            long customerId = account.get().getCustomer().getId();
            accountService.deleteById(account.get().getId());
            appLogger.logInfo(String.format("Рахунок %s клієнта з Id %d видалено успішно", accountRequest.getAccountNumber(), customerId));
            return "Рахунок видалено успішно";
        } else {
            String errorMessage = "Даного акаунту не знайдено";
            errorIndicator = true;
            appLogger.logWarn(errorMessage);
            return errorMessage;
        }
    }

    private String deposit(DepositRequest depositRequest) {
        errorIndicator = false;
        try {
            Optional<Account> maybeAccount = accountService.findByAccountNumber(depositRequest.getAccountNumber());
            if (maybeAccount.isPresent()) {
                Account account = maybeAccount.get();
                account.setBalance(account.getBalance() + depositRequest.getAmount());
                accountService.save(account);
                appLogger.logInfo(String.format("Рахунок з номером %s успішно поповнено на %.2f", depositRequest.getAccountNumber(), depositRequest.getAmount()));
                return "Рахунок поповнено успішно";
            } else {
                String errorMessage = String.format("Не знайдено відповідного рахунку з номером %s", depositRequest.getAccountNumber());
                errorIndicator = true;
                appLogger.logWarn(errorMessage);
                return errorMessage;
            }
        } catch (Exception e) {
            appLogger.logError(e.getMessage(), e);
            errorIndicator = true;
            return "Помилка при поповненні рахунку";
        }
    }
    private String transfer(TransferRequest transferRequest) {
        errorIndicator = false;
        try {
            String message;
            Optional<Account> maybeFromAccount = accountService.findByAccountNumber(transferRequest.getFromAccountNumber());
            Optional<Account> maybeToAccount = accountService.findByAccountNumber(transferRequest.getToAccountNumber());
            if (maybeFromAccount.isPresent() && maybeToAccount.isPresent()) {
                Account fromAccount = maybeFromAccount.get();
                Account toAccount = maybeToAccount.get();
                double convertedSum = Currency.convertTo(fromAccount.getCurrency(),Currency.getFromName(transferRequest.getCurrency()),fromAccount.getBalance());
                if(convertedSum >= transferRequest.getAmount()){
                    fromAccount.setBalance(fromAccount.getBalance() - Currency.convertTo(Currency.getFromName(transferRequest.getCurrency()),fromAccount.getCurrency(),transferRequest.getAmount()));
                    accountService.save(fromAccount);
                    toAccount.setBalance(toAccount.getBalance() +Currency.convertTo(Currency.getFromName(transferRequest.getCurrency()),toAccount.getCurrency(),transferRequest.getAmount()));
                    accountService.save(toAccount);
                    message = "Переказ грошей здійснено успішно";
                    appLogger.logInfo(String.format("Переказ в розмірі %.2f з рахунку %s на рахунок %s здійснено успішно",convertedSum,fromAccount.getAccountNumber(),toAccount.getAccountNumber()));
                }else{
                    message = "На рахунку недостатньо коштів для здійснення переказу";
                    appLogger.logWarn(String.format("На рахунку %s недостатньо коштів для здійснення переказу",fromAccount.getAccountNumber()));
                    errorIndicator = true;
                }
            }
            else if (maybeFromAccount.isPresent()) {
                message = "Введено некоректний номер рахунку отримувача";
                appLogger.logWarn(message);
                errorIndicator = true;
            }
            else if (maybeToAccount.isPresent()) {
                message = "Введено некоректний номер рахунку відправника";
                appLogger.logWarn(message);
                errorIndicator = true;
            }
            else {
                message = "Введено некоректні номери рахунків відправника/отримувача";
                appLogger.logWarn(message);
                errorIndicator = true;
            }
            return message;
        }catch (Exception e) {
            appLogger.logError(e.getMessage(), e);
            errorIndicator = true;
            return "Помилка при переказі коштів";
        }
    }
    private String withdraw(WithdrawRequest withdrawRequest) {
        errorIndicator = false;
        try {
            String message;
            Optional<Account> maybeAccount = accountService.findByAccountNumber(withdrawRequest.getAccountNumber());

            if (maybeAccount.isPresent()) {
                Account account = maybeAccount.get();
                if (account.getBalance() < withdrawRequest.getAmount()) {
                    message = "На рахунку недостатньо коштів для зняття";
                    appLogger.logWarn(String.format("На рахунку %s недостатньо коштів для зняття", account.getAccountNumber()));
                    errorIndicator = true;
                } else {
                    account.setBalance(account.getBalance() - withdrawRequest.getAmount());
                    accountService.save(account);
                    message = "Зняття коштів успішно виконано";
                    appLogger.logInfo(String.format("Зняття %.2f з рахунку %s виконано успішно", withdrawRequest.getAmount(), account.getAccountNumber()));
                }
            } else {
                message = "Введено некоректний номер рахунку";
                appLogger.logWarn(message);
                errorIndicator = true;
            }
            return message;
        } catch (Exception e) {
            appLogger.logError(e.getMessage(), e);
            errorIndicator = true;
            return "Помилка при знятті коштів";
        }
    }
    private static class WebSocketResponse {
        public String status;
        public String message;

        public WebSocketResponse(String status, String message) {
            this.status = status;
            this.message = message;
        }
    }
}
