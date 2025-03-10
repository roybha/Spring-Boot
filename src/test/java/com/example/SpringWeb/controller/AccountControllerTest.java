package com.example.SpringWeb.controller;
import com.example.SpringWeb.DTO.TransferRequest;
import com.example.SpringWeb.facade.AccountFacade;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Currency;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import java.util.Optional;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountFacade accountFacade;

    private Account account;
    private String accNumb;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AccountService accountService() {
            return mock(AccountService.class);
        }

        @Bean
        public AccountFacade accountFacade() {
            return mock(AccountFacade.class);
        }
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers("/**");
        }
    }

    @BeforeEach
    void setUp() {
        account = new Account(1L, Currency.EUR, 100, new Customer(1L));
        account.setAccountNumber(AccountService.generateAccountNumber());
        accNumb = account.getAccountNumber();
        Mockito.when(accountService.findByAccountNumber(accNumb)).thenReturn(Optional.of(account));
        Mockito.when(accountFacade.getAccountByAccountRequest(Mockito.any())).thenReturn(account);
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        Mockito.when(accountFacade.getAccountByAccountRequest(Mockito.any())).thenReturn(account);
        Mockito.when(accountService.save(Mockito.any())).thenReturn(true);
        mockMvc.perform(post("/accounts/create")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("customerId", "1")
                        .param("balance", "100.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/change?id=1"));
    }

    @Test
    void testDeleteAccount_Success() throws Exception {
        Mockito.when(accountService.findByAccountNumber(accNumb)).thenReturn(Optional.of(account));
        mockMvc.perform(post("/accounts/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("accountNumber", accNumb))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    void testDeleteAccount_NotFound() throws Exception {
        Mockito.when(accountService.findByAccountNumber(accNumb)).thenReturn(Optional.empty());
        mockMvc.perform(post("/accounts/delete")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("accountNumber", accNumb))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("message"))
                .andExpect(view().name("error"));
    }

    @Test
    void testDeposit_Success() throws Exception {
        Account account = new Account();
        account.setBalance(100.0);
        account.setCurrency(Currency.GBP);
        String anotherAccNumb = AccountService.generateAccountNumber();
        account.setAccountNumber(anotherAccNumb);
        Mockito.when(accountService.findByAccountNumber(anotherAccNumb)).thenReturn(Optional.of(account));
        Mockito.when(accountService.save(Mockito.any())).thenReturn(true);
        mockMvc.perform(post("/accounts/deposit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("accountNumber", anotherAccNumb)
                        .param("balance", "50.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void testDeposit_Error() throws Exception {
        mockMvc.perform(post("/accounts/deposit")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("accountNumber", "invalid")
                        .param("balance", "50.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void testWithdraw_InsufficientFunds() throws Exception {
        Account account = new Account();
        account.setBalance(50.0);
        account.setCurrency(Currency.GBP);
        String anotherAccNumb = AccountService.generateAccountNumber();
        account.setAccountNumber(anotherAccNumb);
        Mockito.when(accountService.findByAccountNumber(anotherAccNumb)).thenReturn(Optional.of(account));
        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("accountNumber", anotherAccNumb)
                        .param("balance", "100.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void testWithdraw_Success() throws Exception {
        Account account = new Account();
        account.setBalance(100.0);
        account.setCurrency(Currency.GBP);
        String anotherAccNumb = AccountService.generateAccountNumber();
        account.setAccountNumber(anotherAccNumb);
        Mockito.when(accountService.findByAccountNumber(anotherAccNumb)).thenReturn(Optional.of(account));
        mockMvc.perform(post("/accounts/withdraw")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("accountNumber", anotherAccNumb)
                        .param("balance", "50.0"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void testTransfer_Success() throws Exception {
        TransferRequest transferRequest = new TransferRequest();
        String twoAccNumb = AccountService.generateAccountNumber();
        transferRequest.setFromAccountNumber(accNumb);
        transferRequest.setToAccountNumber(twoAccNumb);
        Account anotherAccount = new Account(twoAccNumb,Currency.GBP,40,new Customer(1L));
        transferRequest.setAmount(50.0);
        transferRequest.setCurrency("EUR");
        Mockito.when(accountService.findByAccountNumber(accNumb)).thenReturn(Optional.of(account));
        Mockito.when(accountService.findByAccountNumber(twoAccNumb)).thenReturn(Optional.of(anotherAccount));
        Mockito.when(accountService.save(Mockito.any())).thenReturn(true);
        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fromAccountNumber", accNumb)
                        .param("toAccountNumber", twoAccNumb)
                        .param("amount", "50.0")
                        .param("currency", "EUR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"));
    }

    @Test
    void testTransfer_InsufficientFunds() throws Exception {
        TransferRequest transferRequest = new TransferRequest();
        String twoAccNumb = AccountService.generateAccountNumber();
        transferRequest.setFromAccountNumber(accNumb);
        transferRequest.setToAccountNumber(twoAccNumb);
        Account anotherAccount = new Account(twoAccNumb,Currency.GBP,40,new Customer(1L));
        transferRequest.setAmount(50.0);
        transferRequest.setCurrency("EUR");
        Mockito.when(accountService.findByAccountNumber(twoAccNumb)).thenReturn(Optional.of(anotherAccount));
        Mockito.when(accountService.findByAccountNumber(accNumb)).thenReturn(Optional.of(account));
        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("fromAccountNumber", twoAccNumb)
                        .param("toAccountNumber", accNumb)
                        .param("amount", "100.0")
                        .param("currency", "EUR"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"));
    }

    @Test
    void testOperationPage() throws Exception {
        mockMvc.perform(get("/accounts/operation"))
                .andExpect(status().isOk())
                .andExpect(view().name("account-operations"));
    }


    @Test
    void testDepositPage() throws Exception {
        mockMvc.perform(post("/accounts/deposit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts/deposit"));
    }

    @Test
    void testTransferPage() throws Exception {
        mockMvc.perform(post("/accounts/transfer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts/transfer"));
    }


    @Test
    void testWithdrawPage() throws Exception {
        mockMvc.perform(post("/accounts/withdraw"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/accounts/withdraw"));
    }
}
