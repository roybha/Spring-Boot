package com.example.SpringWeb.controller;
import com.example.SpringWeb.DTO.AccountRequest;
import com.example.SpringWeb.DTO.TransferRequest;
import com.example.SpringWeb.config.AppLogger;
import com.example.SpringWeb.facade.AccountFacade;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Currency;
import com.example.SpringWeb.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Optional;

@Controller
@Slf4j
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AccountFacade accountFacade;
    private final AppLogger appLogger;
    @Autowired
    public AccountController(AccountService accountService,
                             AccountFacade accountFacade,
                             AppLogger appLogger) {
        this.accountService = accountService;
        this.accountFacade = accountFacade;
        this.appLogger = appLogger;
    }
    @GetMapping("/error")
    public String error() {
        return "error";
    }
    @PostMapping("/create")
    public String accounts(@ModelAttribute @Validated AccountRequest accountRequest, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasFieldErrors("balance")) {
            String errorMessage = result.getFieldError("balance").getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            appLogger.logWarn(errorMessage);
            return "redirect:/customers/change?id=" + accountRequest.getCustomerId();
        }
        accountRequest.setAccountNumber(AccountService.generateAccountNumber());
        try {
            if(accountService.save(accountFacade.getAccountByAccountRequest(accountRequest))){
              appLogger.logInfo("Збереження нового акаунта");
              return "redirect:/customers/change?id=" + accountRequest.getCustomerId();
            }
            throw new Exception();
        }catch (Exception e) {
            redirectAttributes.addAttribute("message", "Помилка збереження рахунку");
            appLogger.logError(e.getMessage(),e);
            return "redirect:/accounts/error";
        }
    }



    @PostMapping("/delete")
    public String deleteAccount(@ModelAttribute AccountRequest accountRequest, Model model) {
        String accountNumber = accountRequest.getAccountNumber();
        Optional<Account> account = accountService.findByAccountNumber(accountNumber);
        if(account.isPresent()){
            long customerId = account.get().getCustomer().getId();
            accountService.deleteById(account.get().getId());
            appLogger.logInfo(String.format("Рахунок %s клієнта з Id %d видалено успішно", accountNumber, customerId));
            return "redirect:/customers/change?id=" + customerId;
        }
        else {
            String errorMessage = "Даного акаунту не знайдено";
            model.addAttribute("message", errorMessage);
            appLogger.logWarn(errorMessage);
            return "error";
        }
    }
    @GetMapping("/operation")
    public String operation() {
        return "account-operations";
    }
    @GetMapping("/deposit")
    public String deposit() {
        return "deposit";
    }
    @PostMapping("/deposit")
    public String deposit(
                          @ModelAttribute  AccountRequest accountRequest,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        String errorMessage;
        if(result.hasFieldErrors("balance")) {
            errorMessage = result.getFieldError("balance").getDefaultMessage();
            redirectAttributes.addFlashAttribute("error", errorMessage);
            appLogger.logWarn(errorMessage);
            return "redirect:/accounts/deposit";
        }
        try {
            String accountNumber = accountRequest.getAccountNumber();
            Double balanceUp = accountRequest.getBalance();
            Optional<Account> maybeAccount = accountService.findByAccountNumber(accountNumber);
            if (maybeAccount.isPresent()) {
                Account account = maybeAccount.get();
                account.setBalance(account.getBalance() + balanceUp);
                accountService.save(account);
                redirectAttributes.addFlashAttribute("success", "Рахунок поповнено");
                appLogger.logInfo(String.format("Рахунок з номером %s успішно поповнено на %.2f",accountNumber, balanceUp));
            } else {
                errorMessage =String.format("Не знайдено відповідного рахунку з номером %s", accountNumber);
                redirectAttributes.addFlashAttribute("error", errorMessage);
                appLogger.logWarn(errorMessage);
            }
        } catch (DataAccessException e) {
            errorMessage = e.getMessage();
            redirectAttributes.addFlashAttribute("error", "Помилка доступу до бази даних");
            appLogger.logError(errorMessage,e);
        }
        return "redirect:/accounts/deposit";
    }
    @GetMapping("/transfer")
    public String transfer(Model model) {
        return "transfer";
    }
    @PostMapping("/transfer")
    public String transfer(@ModelAttribute @Validated TransferRequest transferRequest,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        String errorMessage;
        try {
            if(result.hasErrors()) {
                errorMessage = result.getFieldError().getDefaultMessage();
                redirectAttributes.addFlashAttribute("error", errorMessage);
                appLogger.logWarn(errorMessage);
                return "redirect:/accounts/transfer";
            }
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
                    redirectAttributes.addFlashAttribute("success","Переказ грошей здійснено успішно");
                    appLogger.logInfo(String.format("Переказ в розмірі %.2f з рахунку %s на рахунок %s здійснено успішно",convertedSum,fromAccount.getAccountNumber(),toAccount.getAccountNumber()));
                }else{
                    redirectAttributes.addFlashAttribute("error","На рахунку недостатньо коштів для здійснення переказу");
                    appLogger.logWarn(String.format("На рахунку %s недостатньо коштів для здійснення переказу",fromAccount.getAccountNumber()));
                }
            }
            else if (maybeFromAccount.isPresent()) {
                errorMessage = "Введено некоректний номер рахунку отримувача";
                redirectAttributes.addFlashAttribute("error",errorMessage);
                appLogger.logWarn(errorMessage);
            }
            else if (maybeToAccount.isPresent()) {
                errorMessage = "Введено некоректний номер рахунку відправника";
                redirectAttributes.addFlashAttribute("error",errorMessage);
                appLogger.logWarn(errorMessage);
            }
            else {
                errorMessage = "Введено некоректні номери рахунків відправника/отримувача";
                redirectAttributes.addFlashAttribute("error",errorMessage);
                appLogger.logWarn(errorMessage);
            }

        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до бази даних");
            appLogger.logError(e.getMessage(),e);
        }
        return "redirect:/accounts/transfer";
    }

    @GetMapping("/withdraw")
    public String withdraw(Model model) {
        return "withdraw";
    }
    @PostMapping("/withdraw")
    public String withdraw(@ModelAttribute  AccountRequest accountRequest,
                           BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        String errorMessage;
        try{
            if (bindingResult.hasFieldErrors("balance") || bindingResult.hasFieldErrors("accountNumber")) {
                errorMessage = bindingResult.getFieldError().getDefaultMessage();
                redirectAttributes.addAttribute("message",errorMessage);
                appLogger.logWarn(errorMessage);
                return "redirect:accounts/withdraw";
            }
            Optional<Account> maybeAccount = accountService.findByAccountNumber(accountRequest.getAccountNumber());
            if (maybeAccount.isPresent()) {
                Account account = maybeAccount.get();
                if(account.getBalance() < accountRequest.getBalance()){
                    errorMessage = "На рахунку недостатньо коштів";
                    redirectAttributes.addFlashAttribute("error",errorMessage);
                    appLogger.logWarn(errorMessage);
                }
                else {
                    account.setBalance(account.getBalance() - accountRequest.getBalance());
                    accountService.save(account);
                    redirectAttributes.addFlashAttribute("success","Гроші успішно зняті з рахунку");
                    appLogger.logInfo("Гроші успішно зняті з рахунку");
                }
            }
            else {
                errorMessage = "Введено некоректний номер рахунку";
                redirectAttributes.addFlashAttribute(errorMessage);
                appLogger.logWarn(errorMessage);
            }
        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error", "Помилка доступу до бази даних");
            appLogger.logError(e.getMessage(),e);
        }
        return "redirect:/accounts/withdraw";
    }


}
