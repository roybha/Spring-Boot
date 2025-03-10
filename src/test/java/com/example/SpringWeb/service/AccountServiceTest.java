package com.example.SpringWeb.service;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Currency;
import com.example.SpringWeb.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;


    @InjectMocks
    private AccountService accountService;

    private Account account;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        account = new Account();
        account.setId(1L);
        account.setAccountNumber("ACC123456789");
        account.setBalance(1000.0);
        account.setCurrency(Currency.USD);
    }

    @Test
    void testSave() {
        when(accountRepository.save(account)).thenReturn(account);
        boolean result = accountService.save(account);
        assertTrue(result);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testSaveFails() {
        when(accountRepository.save(account)).thenThrow(new DataIntegrityViolationException("DB Error"));
        boolean result = accountService.save(account);
        assertFalse(result);
        verify(accountRepository, times(1)).save(account);
    }

    @Test
    void testDelete() {
        doNothing().when(accountRepository).delete(account);
        boolean result = accountService.delete(account);
        assertTrue(result);
        verify(accountRepository, times(1)).delete(account);
    }


    @Test
    void testDeleteFails() {
        doThrow(new DataIntegrityViolationException("DB Error") {}).when(accountRepository).delete(account);
        boolean result = accountService.delete(account);
        assertFalse(result);
        verify(accountRepository, times(1)).delete(account);
    }

    @Test
    void testFindAll() {
        when(accountRepository.findAll()).thenReturn(List.of(account));
        List<Account> result = accountService.findAll();
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(accountRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
        Optional<Account> result = accountService.findById(1L);
        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(accountRepository.findById(1L)).thenReturn(Optional.empty());
        Optional<Account> result = accountService.findById(1L);
        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() {
        doNothing().when(accountRepository).deleteById(1L);
        boolean result = accountService.deleteById(1L);
        assertTrue(result);
        verify(accountRepository, times(1)).deleteById(1L);
    }


    @Test
    void testDeleteByIdFails() {
        doThrow(new DataIntegrityViolationException("DB Error") {}).when(accountRepository).deleteById(1L);
        boolean result = accountService.deleteById(1L);
        assertFalse(result);
        verify(accountRepository, times(1)).deleteById(1L);
    }


    @Test
    void testFindByAccountNumber() {
        when(accountRepository.findByAccountNumber("ACC123456789")).thenReturn(Optional.of(account));
        Optional<Account> result = accountService.findByAccountNumber("ACC123456789");
        assertTrue(result.isPresent());
        assertEquals(account, result.get());
        verify(accountRepository, times(1)).findByAccountNumber("ACC123456789");
    }

    @Test
    void testFindByAccountNumberNotFound() {
        when(accountRepository.findByAccountNumber("ACC123456789")).thenReturn(Optional.empty());
        Optional<Account> result = accountService.findByAccountNumber("ACC123456789");
        assertFalse(result.isPresent());
        verify(accountRepository, times(1)).findByAccountNumber("ACC123456789");
    }

    @Test
    void testGenerateAccountNumber() {
        String generatedAccountNumber = AccountService.generateAccountNumber();
        assertNotNull(generatedAccountNumber);
        assertTrue(generatedAccountNumber.startsWith("ACC"));
        assertEquals(12, generatedAccountNumber.length() - 3);
    }
}
