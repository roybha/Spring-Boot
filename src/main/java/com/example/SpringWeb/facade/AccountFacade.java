package com.example.SpringWeb.facade;

import com.example.SpringWeb.DTO.AccountRequest;
import com.example.SpringWeb.DTO.AccountResponse;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.service.AccountService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class AccountFacade {
    @Autowired
    ModelMapper modelMapper;

    public Account getAccountByAccountRequest(AccountRequest accountRequest) {
       return modelMapper.map(accountRequest, Account.class);
    }
    public AccountResponse getAccountResponseByAccount(Account account) {
        return modelMapper.map(account, AccountResponse.class);
    }

}
