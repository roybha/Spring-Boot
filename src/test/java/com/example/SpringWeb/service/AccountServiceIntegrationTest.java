package com.example.SpringWeb.service;

import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Currency;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AccountServiceIntegrationTest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    private Account testAccount;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        accountRepository.deleteAll();
        testCustomer = new Customer(1L,"Some Name","Some Surname","email@gmail.com",19,new ArrayList<>());
        testAccount = new Account();
        testAccount.setAccountNumber(AccountService.generateAccountNumber());
        testAccount.setBalance(1000.0);
        testAccount.setCurrency(Currency.USD);
        testAccount.setCustomer(testCustomer);
        testAccount = accountRepository.save(testAccount);
    }

    @Test
    void testSave() {
        Account newAccount = new Account();
        newAccount.setAccountNumber(AccountService.generateAccountNumber());
        newAccount.setBalance(500.0);
        newAccount.setCurrency(Currency.EUR);
        newAccount.setCustomer(testCustomer);
        boolean isSaved = accountService.save(newAccount);
        assertTrue(isSaved);
        assertTrue(accountRepository.findById(newAccount.getId()).isPresent());
    }

    @Test
    void testDelete() {
        boolean isDeleted = accountService.delete(testAccount);
        assertTrue(isDeleted);
        assertFalse(accountRepository.findById(testAccount.getId()).isPresent());
    }

    @Test
    void testFindAll() {
        List<Account> accounts = accountService.findAll();
        assertEquals(1, accounts.size());
        assertTrue(accounts.contains(testAccount));
    }

    @Test
    void testFindById() {
        Optional<Account> foundAccount = accountService.findById(testAccount.getId());
        assertTrue(foundAccount.isPresent());
        assertEquals(testAccount.getId(), foundAccount.get().getId());
    }

    @Test
    void testDeleteById() {
        boolean isDeleted = accountService.deleteById(testAccount.getId());
        assertTrue(isDeleted);
        assertFalse(accountRepository.findById(testAccount.getId()).isPresent());
    }

    @Test
    void testFindByAccountNumber() {
        Optional<Account> foundAccount = accountService.findByAccountNumber(testAccount.getAccountNumber());
        assertTrue(foundAccount.isPresent());
        assertEquals(testAccount.getAccountNumber(), foundAccount.get().getAccountNumber());
    }
}

