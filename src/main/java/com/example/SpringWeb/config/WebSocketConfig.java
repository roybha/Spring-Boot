package com.example.SpringWeb.config;

import com.example.SpringWeb.facade.AccountFacade;
import com.example.SpringWeb.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    @Autowired
    private final AccountService accountService;
    @Autowired
    private final AccountFacade accountFacade;
    @Autowired
    private final AppLogger appLogger;
    @Autowired
    private final ObjectMapper objectMapper;

    private final AccountWebSocketHandler accountWebSocketHandler;

    public WebSocketConfig(AccountService accountService, AccountFacade accountFacade,AppLogger appLogger, ObjectMapper objectMapper) {
        this.accountService = accountService;
        this.accountFacade = accountFacade;
        this.appLogger = appLogger;
        this.objectMapper = objectMapper;
        this.accountWebSocketHandler = new AccountWebSocketHandler(
                this.accountService,this.accountFacade,
                this.appLogger,this.objectMapper
        );
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(accountWebSocketHandler, "/ws/accounts/create")
                .setAllowedOrigins("*");
        registry.addHandler(accountWebSocketHandler, "/ws/accounts/delete")
                .setAllowedOrigins("*");
        registry.addHandler(accountWebSocketHandler, "/ws/accounts/deposit")
                .setAllowedOrigins("*");
        registry.addHandler(accountWebSocketHandler, "/ws/accounts/withdraw")
                .setAllowedOrigins("*");
        registry.addHandler(accountWebSocketHandler, "/ws/accounts/transfer")
                .setAllowedOrigins("*");
    }
}
