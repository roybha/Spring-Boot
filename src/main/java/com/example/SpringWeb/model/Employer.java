package com.example.SpringWeb.model;

import jakarta.persistence.*;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Entity
@Table(name = "employers")
public class Employer extends AbstractEntity {
    public Employer() {

    }
    public Employer(Long id, String name, String address) {
        this.id = id;
        this.name = name;
        this.address = address;
    }
    public Employer(String name, String address) {
        this.name = name;
        this.address = address;
    }
    public Employer(Long id){
        this.id = id;
    }

    @Column(unique = true, nullable = false)
    String name;
    @Column( nullable = false)
    String address;
    @ManyToMany
    @JoinTable(
            name = "customer_employer",
            joinColumns = @JoinColumn(name = "employer_id"),
            inverseJoinColumns = @JoinColumn(name = "customer_id")
    )
    private List<Customer> customers;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public List<Customer> getCustomers() {
        return customers;
    }
    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

}
