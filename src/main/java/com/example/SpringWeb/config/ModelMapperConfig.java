package com.example.SpringWeb.config;
import com.example.SpringWeb.DTO.AccountRequest;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Currency;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.service.CustomerService;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper modelMapper(CustomerService customerService) {
        ModelMapper modelMapper = new ModelMapper();
        Converter<String, Currency> currencyConverter = context -> Currency.valueOf(context.getSource().toUpperCase());

        Converter<Long, Customer> customerConverter = context -> customerService.findById(context.getSource()).get();

        modelMapper.typeMap(AccountRequest.class, Account.class)
                .addMappings(mapper -> {
                    mapper.using(currencyConverter).map(AccountRequest::getCurrency, Account::setCurrency);
                    mapper.using(customerConverter).map(AccountRequest::getCustomerId, Account::setCustomer);
                });

        return modelMapper;
    }
}

