package com.example.SpringWeb.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class FaviconController {
    @RequestMapping("favicon.ico")
    public void returnFavicon(HttpServletResponse response) {
        response.setStatus(HttpServletResponse.SC_OK);
    }
}

