package com.example.SpringWeb.facade;

import com.example.SpringWeb.DTO.EmployerResponse;
import com.example.SpringWeb.model.Employer;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmployerFacade {
    @Autowired
    ModelMapper modelMapper;
    public EmployerResponse getEmployerResponseByEmployer(Employer employer) {
        return modelMapper.map(employer, EmployerResponse.class);
    }
}
