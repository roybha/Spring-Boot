package com.example.SpringWeb.controller;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MenuController {
    @GetMapping("menu")
    public String mainMenu() {
        return "menu_page";
    }
}
