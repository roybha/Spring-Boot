package com.example.SpringWeb.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Entity
@Table(name = "employers")
@Data // Автоматично генерує гетери, сетери, toString(), equals(), hashCode() для всіх полів
@NoArgsConstructor // Генерує конструктор без аргументів
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Employer extends AbstractEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @ManyToMany
    @JoinTable(
            name = "customer_employer",
            joinColumns = @JoinColumn(name = "employer_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<Customer> customers;
    public Employer(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
