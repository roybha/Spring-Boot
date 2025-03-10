package com.example.SpringWeb.DTO;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.*;

@Setter
@Getter
public class AdminRequest {

    @NotNull(message = "Ім'я користувача не може бути порожнім.")
    @Size(min = 4, message="Ім'я має містити щонайменше 4 символи")
    private String username;

    @NotNull(message = "Пароль не може бути порожнім.")
    @Size(min = 8, message = "Пароль має бути не менше 8 символів.")
    private String password;
}
