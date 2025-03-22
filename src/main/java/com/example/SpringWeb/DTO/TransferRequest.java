package com.example.SpringWeb.DTO;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TransferRequest {
    @NotBlank(message = "Треба вказати номер відправника")
    private String fromAccountNumber;
    @NotBlank(message = "Треба вказати номер отримувача")
    private String toAccountNumber;
    @NotNull(message = "Треба вказати суму переказу")
    @Positive(message = "Сума має бути більше за 0")
    private Double amount;
    @NotBlank(message = "Валюта не може бути порожньою")
    @Pattern(regexp = "USD|EUR|UAH|GBP|CHF", message = "Дозволені валюти: USD, EUR, UAH,GBP,CHF")
    private String currency;
}
