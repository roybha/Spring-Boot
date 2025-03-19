package com.example.SpringWeb.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@ControllerAdvice
public class AppLogger {
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(IllegalArgumentException ex) {
        logWarn("Клієнтська помилка: " + ex.getMessage());
        return "Некоректні дані: " + ex.getMessage();
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleUnknownError(Exception ex) {
        logError("Невідома помилка", ex);
        return "Сталася помилка. Зверніться до адміністратора.";
    }
    public void logError(String message, Throwable ex) {
        if (ex != null) {
            log.error(message, ex);
        } else {
            log.error(message);
        }
    }

    public void logWarn(String message) {
        log.warn(message);
    }
    public void logInfo(String message) {
        log.info(message);
    }
}
