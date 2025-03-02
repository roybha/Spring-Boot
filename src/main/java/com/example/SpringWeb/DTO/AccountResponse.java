package com.example.SpringWeb.DTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountResponse {

    private Long id;
    private String accountNumber;
    private String currency;
    private Double balance;
    private Long customerId;
}

