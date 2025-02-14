package com.example.SpringWeb.model;
import com.example.SpringWeb.converter.CurrencyConverter;
import jakarta.persistence.*;
import org.springframework.stereotype.Component;


@Component
@Entity
@Table(name = "accounts")
public class Account extends AbstractEntity {

    @Column(name = "account_number", unique = true, nullable = false, length = 50)
    private String accountNumber;

    @Convert(converter = CurrencyConverter.class) // Якщо `Currency` — це `enum`, інакше треба `@ManyToOne`
    @Column(nullable = false)
    private Currency currency;

    @Column(nullable = false)
    private double balance;

    @ManyToOne // Багато рахунків можуть належати одному клієнту
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    public Account() {

    }
    public Account(Currency currency, Customer customer) {
        this.currency = currency;
        this.customer = customer;
    }
    public Account(Currency currency, double balance, Customer customer) {
        this.currency = currency;
        this.balance = balance;
        this.customer = customer;
    }
    public Account(long id, Currency currency, double balance, Customer customer) {
        this.setId(id);
        this.currency = currency;
        this.balance = balance;
        this.customer = customer;
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
    public Customer getCustomer() {
        return customer;
    }
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

}
