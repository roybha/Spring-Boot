package com.example.SpringWeb.controller;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class MenuControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    public void testRegisterAndRedirectToLogin() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "testuser")
                        .param("password", "testpass")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("message", "Реєстрація успішна! Тепер ви можете увійти."));
    }

    @Test
    public void testRegisterLoginAndAccessMenu() throws Exception {
        mockMvc.perform(post("/register")
                        .param("username", "testuser1")
                        .param("password", "testpass")
                        .param("confirmPassword", "testpass"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        mockMvc.perform(post("/login")
                        .param("username", "testuser1")
                        .param("password", "testpass"))
                .andExpect(status().is3xxRedirection()) // Очікуємо редірект на меню
                .andExpect(redirectedUrl("/menu"));
    }
    @Test
    public void testAccessMenuWithoutLogin() throws Exception {
        mockMvc.perform(get("/menu"))
                .andExpect(status().isUnauthorized());
    }
}
