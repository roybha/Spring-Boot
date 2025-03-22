package com.example.SpringWeb.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DepositRequest {
    @NotBlank(message = "Рахунок не може бути не вказаним")
    @Size(max = 50, message = "Номер рахунку не має перевищувати 50 символів")
    private String accountNumber;
    @NotNull(message = "Баланс відсутній")
    @Min(value = 0,message = "Баланс не може бути від'ємним")
    private Double amount;
}
