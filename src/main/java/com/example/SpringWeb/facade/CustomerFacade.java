package com.example.SpringWeb.facade;

import com.example.SpringWeb.DTO.CustomerRequest;
import com.example.SpringWeb.DTO.CustomerResponse;
import com.example.SpringWeb.model.Customer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomerFacade {
    @Autowired
    private ModelMapper modelMapper;

    public CustomerResponse getCustomerResponseByCustomer(Customer customer) {
        return modelMapper.map(customer, CustomerResponse.class);
    }
    public Customer getCustomerByCustomerRequest(CustomerRequest customerRequest) {
        return modelMapper.map(customerRequest, Customer.class);
    }

}
