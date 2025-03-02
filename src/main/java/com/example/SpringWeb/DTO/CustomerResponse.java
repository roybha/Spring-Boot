package com.example.SpringWeb.DTO;

import com.example.SpringWeb.model.AbstractEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
public class CustomerResponse extends AbstractEntity {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private int age;
    private String phoneNumber;
    private List<AccountResponse> accounts;
    private List<EmployerResponse> employers;
}
