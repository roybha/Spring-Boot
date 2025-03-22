package com.example.SpringWeb.DTO;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AccountRequest {
    @Min(value = 1,message="ID не може убути менше 1")
    private Long id;

    @NotBlank(message = "Рахунок не може бути не вказаним")
    @Size(max = 50, message = "Номер рахунку не має перевищувати 50 символів")
    private String accountNumber;

    @NotNull(message = "Валюта не вказана")
    private String currency;

    @NotNull(message = "Баланс відсутній")
    @Min(value = 0,message = "Баланс не може бути від'ємним")
    private Double balance;

    @NotNull(message = "Id-користувача не може бути відсутнім")
    private Long customerId;
    public  AccountRequest(String accountNumber){
        this.accountNumber = accountNumber;
    }
    public AccountRequest(String currency,Double balance,Long customerId){
        this.currency = currency;
        this.balance = balance;
        this.customerId = customerId;
    }
}
