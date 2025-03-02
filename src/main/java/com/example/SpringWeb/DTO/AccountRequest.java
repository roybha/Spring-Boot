package com.example.SpringWeb.DTO;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
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
}
