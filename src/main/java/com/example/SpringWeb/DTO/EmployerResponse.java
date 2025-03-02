package com.example.SpringWeb.DTO;

import com.example.SpringWeb.model.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class EmployerResponse extends AbstractEntity {
    private String name;
    private String address;
    private List<CustomerResponse> customers;
}
