package com.example.SpringWeb.controller;
import com.example.SpringWeb.config.AppLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class LoginController {
    private final AppLogger appLogger;
    @Autowired
    public LoginController(AppLogger appLogger) {
        this.appLogger = appLogger;
    }

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error, Model model) {
        String message;
        if (error != null) {
            message = "Невірне ім'я користувача або пароль.";
            model.addAttribute("error", message);
            appLogger.logWarn(message);
        }
        return "login";
    }
}

