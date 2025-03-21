package com.example.SpringWeb.model;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Currency {
    EUR(1.0,"EUR"),
    USD(1.18,"USD"),
    UAH(42.0,"UAH"),
    CHF(0.91,"CHF"),
    GBP(0.87,"GBP");

    private final double rateToEUR;
    private final String name;// Курс до EUR

    // Метод для конвертації з будь-якої валюти до EUR
    public double toEUR(double value) {
        return value / rateToEUR;
    }

    // Метод для конвертації з EUR до іншої валюти
    public double fromEUR(double value) {
        return value * rateToEUR;
    }

    // Конвертація з однієї валюти в іншу
    public  static double convertTo(Currency fromCurrency, Currency toCurrency, double value) {
        // Спочатку переводимо в EUR, потім з EUR в targetCurrency
        double valueInEUR = fromCurrency.toEUR(value);
        return toCurrency.fromEUR(valueInEUR);
    }
    public static Currency getFromName(String currencyName) {

        return switch (currencyName) {
            case "EUR" -> EUR;
            case "USD" -> USD;
            case "UAH" -> UAH;
            case "CHF" -> CHF;
            case "GBP" -> GBP;
            default -> EUR;
        };
    }

    @Override
    public String toString() {
        return name;
    }
}

