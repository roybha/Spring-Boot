package com.example.SpringWeb.facade;
import com.example.SpringWeb.DTO.AdminRequest;
import com.example.SpringWeb.model.Admin;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AdminFacade {
    @Autowired
    private ModelMapper modelMapper;
    public Admin getAdminByAdminRequest(AdminRequest adminRequest) {
        return modelMapper.map(adminRequest, Admin.class);
    }

}
