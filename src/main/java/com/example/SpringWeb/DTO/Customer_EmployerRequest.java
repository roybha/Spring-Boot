package com.example.SpringWeb.DTO;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Getter
@Setter
public class Customer_EmployerRequest{
    @Min(value=1,message="ID користувача має бути не менше 1")
    private Long customerId;
    @NotBlank(message="Відсутня назва компанії")
    @Size(min=3,message="Ім'я компанії має містити щонайменше 3 символи")
    private String employerName;
}
