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
public class WithdrawRequest {
    @NotBlank(message = "Рахунок не може бути не вказаним")
    @Size(max = 50, message = "Номер рахунку не має перевищувати 50 символів")
    private String accountNumber;

    @NotNull(message = "Сума відсутня")
    @Min(value = 0,message = "Сума не може бути від'ємною")
    private Double amount;
}
