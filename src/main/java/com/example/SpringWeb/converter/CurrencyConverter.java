package com.example.SpringWeb.converter;

import com.example.SpringWeb.model.Currency;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;


@Converter(autoApply = true)
public class CurrencyConverter implements AttributeConverter<Currency, String> {

    @Override
    public String convertToDatabaseColumn(Currency currency) {
        return currency != null ? currency.toString() : null;
    }

    @Override
    public Currency convertToEntityAttribute(String dbData) {
        return dbData != null ? Currency.getFromName(dbData) : null;
    }
}
