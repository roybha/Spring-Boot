package com.example.SpringWeb.controller;

import com.example.SpringWeb.DTO.AdminRequest;
import com.example.SpringWeb.service.AdminService;
import com.example.SpringWeb.facade.AdminFacade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private AdminRequest adminRequest;

    @BeforeEach
    void setUp() {
        adminRequest = new AdminRequest();
        adminRequest.setUsername("admin");
        adminRequest.setPassword("password123");
    }

    @Test
    void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("admin"));
    }

    @Test
    void testRegisterUserWithValidData() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", adminRequest.getUsername())
                        .param("password", adminRequest.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Реєстрація успішна! Тепер ви можете увійти."));
    }

    @Test
    void testRegisterUserWithValidationErrors() throws Exception {
        adminRequest.setUsername("");
        mockMvc.perform(post("/register")
                        .param("username", adminRequest.getUsername())
                        .param("password", adminRequest.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/register"))
                .andExpect(flash().attributeExists("error"));
    }
}
