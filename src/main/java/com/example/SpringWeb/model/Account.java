package com.example.SpringWeb.model;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Account{
    private Long id;
    private String accountNumber;
    private Currency currency;
    private double balance;
    private Long customer;
    public Account() {

    }
    public Account(Currency currency, Long customer) {
        this.currency = currency;
        this.customer = customer;
    }
    public Account(Currency currency, double balance, Long customer) {
        this.currency = currency;
        this.balance = balance;
        this.customer = customer;
    }
    public Account(long id, Currency currency, double balance, Long customer) {
        this.id = id;
        this.currency = currency;
        this.balance = balance;
        this.customer = customer;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Currency getCurrency() {
        return currency;
    }
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }
    public double getBalance() {
        return balance;
    }
    public void setBalance(double balance) {
        this.balance = balance;
    }
    public Long getCustomer() {
        return customer;
    }
    public void setCustomer(Long customer) {
        this.customer = customer;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
