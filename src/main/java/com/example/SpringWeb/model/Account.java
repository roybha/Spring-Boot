package com.example.SpringWeb.model;
import com.example.SpringWeb.converter.CurrencyConverter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.stereotype.Component;


@Component
@Entity
@Table(name = "accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
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



    public Account(Currency currency, Customer customer) {
        this.currency = currency;
        this.customer = customer;
    }
    public Account(Currency currency, double balance, Customer customer) {
        this.currency = currency;
        this.balance = balance;
        this.customer = customer;
    }
    public Account(Long id, Currency currency, double balance, Customer customer) {
        this.setId(id);
        this.currency = currency;
        this.balance = balance;
        this.customer = customer;
    }
    @Override
    public String toString() {
        return "Account{" +
                "accountNumber='" + accountNumber + '\'' +
                ", currency=" + currency +
                ", balance=" + balance +
                '}';
    }

}
