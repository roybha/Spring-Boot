package com.example.SpringWeb.DTO;
import com.example.SpringWeb.model.AbstractEntity;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomerRequest{
    @Min(value = 1,message="ID користувача не може бути меншим за 1")
    private Long id;
    @Size(min = 2, message = "Ім'я має бути не менше 2 символів")
    @NotBlank(message = "Ім'я не може бути порожнім")
    private String name;

    @Size(min = 2, message = "Прізвище має бути не менше 2 символів")
    @NotBlank(message = "Прізвище не може бути порожнім")
    private String surname;

    @NotBlank(message = "Електронна пошта не може бути порожньою")
    @Email(message = "Невірний формат електронної пошти")
    private String email;

    @Min(value = 18, message = "Вік має бути не менше 18 років")
    private Integer age;

    @NotBlank(message = "Пароль не може бути порожнім")
    @Size(min=8,message="Пароль не може бути менше 8 символів")
    private String password;

    @NotBlank(message = "Номер телефону не може бути порожнім")
    @Pattern(regexp = "^[+]?\\d{1,4}?[\\s\\-]?(?:\\(?\\d{1,4}?\\)?[\\s\\-]?)?\\d{1,9}$", message = "Невірний формат номера телефону")
    private String phoneNumber;
}
