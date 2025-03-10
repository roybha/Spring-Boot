package com.example.SpringWeb.controller;
import com.example.SpringWeb.DTO.AccountRequest;
import com.example.SpringWeb.DTO.TransferRequest;
import com.example.SpringWeb.facade.AccountFacade;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Currency;
import com.example.SpringWeb.service.AccountService;
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
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;
    private final AccountFacade accountFacade;
    @Autowired
    public AccountController(AccountService accountService, AccountFacade accountFacade) {
        this.accountService = accountService;
        this.accountFacade = accountFacade;

    }
    @GetMapping("/error")
    public String error() {
        return "error";
    }
    @PostMapping("/create")
    public String accounts(@ModelAttribute @Validated AccountRequest accountRequest, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasFieldErrors("balance")) {
            redirectAttributes.addFlashAttribute("error", result.getFieldError("balance").getDefaultMessage());
            return "redirect:/customers/change?id=" + accountRequest.getCustomerId();
        }
        accountRequest.setAccountNumber(AccountService.generateAccountNumber());
        try {
            if(accountService.save(accountFacade.getAccountByAccountRequest(accountRequest)))
              return "redirect:/customers/change?id=" + accountRequest.getCustomerId();
            throw new Exception();
        }catch (Exception e) {
            redirectAttributes.addAttribute("message", "Помилка збереження рахунку");
            return "redirect:/accounts/error";
        }
    }



    @PostMapping("/delete")
    public String deleteAccount(@ModelAttribute AccountRequest accountRequest, Model model) {
        Optional<Account> account = accountService.findByAccountNumber(accountRequest.getAccountNumber());
        if(account.isPresent()){
            long customerId = account.get().getCustomer().getId();
            accountService.deleteById(account.get().getId());
            return "redirect:/customers/change?id=" + customerId;
        }
        else {
            model.addAttribute("message", "Даного акаунту не знайдено");
            return "error";
        }
    }
    @GetMapping("/operation")
    public String operation(Model model) {
        return "account-operations";
    }
    @GetMapping("/deposit")
    public String deposit(Model model) {
        return "deposit";
    }
    @PostMapping("/deposit")
    public String deposit(
                          @ModelAttribute  AccountRequest accountRequest,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if(result.hasFieldErrors("balance")) {
            redirectAttributes.addFlashAttribute("error", result.getFieldError("balance").getDefaultMessage());
            return "redirect:/accounts/deposit";
        }
        try {
            Optional<Account> maybeAccount = accountService.findByAccountNumber(accountRequest.getAccountNumber());
            if (maybeAccount.isPresent()) {
                Account account = maybeAccount.get();
                account.setBalance(account.getBalance() + accountRequest.getBalance());
                accountService.save(account);
                redirectAttributes.addFlashAttribute("success", "Рахунок поповнено");
            } else {
                redirectAttributes.addFlashAttribute("error", "Не знайдено відповідного рахунку");
            }
        } catch (DataAccessException e) {
            redirectAttributes.addFlashAttribute("error", "Помилка доступу до бази даних");
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
        try {
            if(result.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", result.getFieldError().getDefaultMessage());
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
                }else{
                    redirectAttributes.addFlashAttribute("error","На рахунку недостатньо коштів для здійснення переказу");
                }
            }
            else if (maybeFromAccount.isPresent()) {
                redirectAttributes.addFlashAttribute("error","Введено некоректний номер рахунку отримувача");
            }
            else if (maybeToAccount.isPresent()) {
                redirectAttributes.addFlashAttribute("error","Введено некоректний номер рахунку відправника");
            }
            else {
                redirectAttributes.addFlashAttribute("error","Введено некоректні номери рахунків відправника/отримувача");
            }

        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до бази даних");
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
        try{
            if (bindingResult.hasFieldErrors("balance") || bindingResult.hasFieldErrors("accountNumber")) {
                redirectAttributes.addAttribute("message",bindingResult.getFieldError().getDefaultMessage());
                return "redirect:accounts/withdraw";
            }
            Optional<Account> maybeAccount = accountService.findByAccountNumber(accountRequest.getAccountNumber());
            if (maybeAccount.isPresent()) {
                Account account = maybeAccount.get();
                if(account.getBalance() < accountRequest.getBalance()){
                    redirectAttributes.addFlashAttribute("error","На рахунку недостатньо коштів");
                }
                else {
                    account.setBalance(account.getBalance() - accountRequest.getBalance());
                    accountService.save(account);
                    redirectAttributes.addFlashAttribute("success","Гроші успішно зняті з рахунку");
                }
            }
            else {
                redirectAttributes.addFlashAttribute("Введено некоректний номер рахунку");
            }
        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error", "Помилка доступу до бази даних");
        }
        return "redirect:/accounts/withdraw";
    }


}
