package com.example.SpringWeb.controller;

import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Currency;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/accounts")
public class AccountController {
    private final AccountService accountService;
    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }
    @PostMapping("/create")
    public String accounts(@RequestParam(name = "balance") double balance,
                           @RequestParam(name = "currency") Currency currency,
                           @RequestParam(name = "customerId") long customerId,
                           Model model) {
        Account account = new Account(currency, balance, new Customer(customerId));
        account.setAccountNumber(AccountService.generateAccountNumber());
        if(accountService.save(account)){
            return "redirect:/customers/change?id=" + customerId;
        }else {
            model.addAttribute("message", "Не вдалося зберегти акаунт");
            return "error";
        }
    }
    @PostMapping("/delete")
    public String deleteAccount(@RequestParam(name = "accountNumber") String accountNumber, Model model) {
        Optional<Account> account = accountService.findByAccountNumber(accountNumber);
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
    public String deposit(@RequestParam(name = "amount") double amount,
                          @RequestParam(name = "accountNumber") String accountNumber,
                          RedirectAttributes redirectAttributes) {
        try {
            Optional<Account> maybeAccount = accountService.findByAccountNumber(accountNumber);
            if (maybeAccount.isPresent()) {
                Account account = maybeAccount.get();
                account.setBalance(account.getBalance() + amount);
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
    public String transfer(@RequestParam(name = "fromAccountNumber") String fromAccountNumber,
                           @RequestParam(name = "toAccountNumber") String toAccountNumber,
                           @RequestParam(name = "amount") double amount,
                           @RequestParam(name = "currency") Currency currency,
                           RedirectAttributes redirectAttributes) {
        try {
            Optional<Account> maybeFromAccount = accountService.findByAccountNumber(fromAccountNumber);
            Optional<Account> maybeToAccount = accountService.findByAccountNumber(toAccountNumber);
            if (maybeFromAccount.isPresent() && maybeToAccount.isPresent()) {
                Account fromAccount = maybeFromAccount.get();
                Account toAccount = maybeToAccount.get();
                double convertedSum = Currency.convertTo(fromAccount.getCurrency(),currency,fromAccount.getBalance());
                if(convertedSum >= amount){
                    fromAccount.setBalance(fromAccount.getBalance() - Currency.convertTo(currency,fromAccount.getCurrency(),amount));
                    accountService.save(fromAccount);
                    toAccount.setBalance(toAccount.getBalance() +Currency.convertTo(currency,toAccount.getCurrency(),amount));
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
    public String withdraw(@RequestParam(name = "accountNumber") String accountNumber,
                           @RequestParam(name = "amount") double amout,
                           RedirectAttributes redirectAttributes) {
        try{
            Optional<Account> maybeAccount = accountService.findByAccountNumber(accountNumber);
            if (maybeAccount.isPresent()) {
                Account account = maybeAccount.get();
                if(account.getBalance() < amout){
                    redirectAttributes.addFlashAttribute("error","На рахунку недостатньо коштів");
                }
                else {
                    account.setBalance(account.getBalance() - amout);
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
