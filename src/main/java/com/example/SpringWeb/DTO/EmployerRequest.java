package com.example.SpringWeb.DTO;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployerRequest {
    private Long id;
    @NotBlank(message="Не введено назву  компанії")
    @Size(min=3,message ="Ім'я компанії має містити щонайменше 3 символи")
    private String name;

    @NotBlank(message="Не введено адресу  компанії")
    @Size(min=3,message ="Адреса компанії має містити щонайменше 3 символи")
    private String address;

    private List<CustomerRequest> customers;
}
