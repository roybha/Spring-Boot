package com.example.SpringWeb.controller;
import com.example.SpringWeb.DTO.CustomerRequest;
import com.example.SpringWeb.DTO.CustomerResponse;
import com.example.SpringWeb.DTO.Customer_EmployerRequest;
import com.example.SpringWeb.facade.CustomerFacade;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.service.AccountService;
import com.example.SpringWeb.service.CustomerService;
import com.example.SpringWeb.service.Customer_EmployerService;
import com.example.SpringWeb.service.EmployerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private Customer_EmployerService cEService;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private CustomerFacade customerFacade;

    private Customer customer;
    private List<Account> accounts;
    private List<Employer> employers;
    private Long customerId;

    @TestConfiguration
    static class TestConfig {
        @Bean(name = "customerService")
        public CustomerService customerService() {
            return mock(CustomerService.class);
        }

        @Bean(name = "accountService")
        public AccountService accountService() {
            return mock(AccountService.class);
        }

        @Bean(name = "customerFacade")
        public CustomerFacade customerFacade() {
            return mock(CustomerFacade.class);
        }
        @Bean
        @Primary
        public Customer_EmployerService cEService() {
            return mock(Customer_EmployerService.class);
        }
        @Bean
        public EmployerService employerService() {
            return mock(EmployerService.class);
        }
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers("/**");
        }

    }

    @BeforeEach
    void setUp() {
        customer = new Customer("Finn", "Starkarm", "finn.starkarm@gmail.com", 30, "somePass", "+38095632482", new ArrayList<>(), new ArrayList<>());
        customer.setId(1L);
        customerId = customer.getId();
        accounts = new ArrayList<>();
        employers = new ArrayList<>();
        Mockito.when(customerService.findById(customerId)).thenReturn(Optional.ofNullable(customer));
        Mockito.when(customerService.save(any())).thenReturn(true);
    }

    @Test
    void testCreateCustomer_Success() throws Exception {
        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("name",customer.getName())
                .param("surname",customer.getSurname())
                .param("email",customer.getEmail())
                .param("age", String.valueOf(customer.getAge()))
                .param("phoneNumber",customer.getPhoneNumber())
                .param("password",customer.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/create"));
    }
    @Test
    void testCreateCustomer_UnSuccess() throws Exception {
        mockMvc.perform(post("/customers/add")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", customer.getName())
                        .param("surname", customer.getSurname())
                        .param("email", customer.getEmail())
                        .param("age", String.valueOf(13))
                        .param("phoneNumber", customer.getPhoneNumber())
                        .param("password", customer.getPassword()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/customers/error*"))
                .andExpect(result -> {
                    String redirectedUrl = result.getResponse().getRedirectedUrl();
                    assertTrue(redirectedUrl.contains("message="));
                });

    }


    @Test
    void testDeleteCustomer_NotFound() throws Exception {
        Mockito.when(customerService.deleteById(customerId)).thenReturn(false);
        mockMvc.perform(post("/customers/delete/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("edit-customer"));
    }

    @Test
    void testDeleteCustomer_Success() throws Exception {
        Mockito.when(customerService.deleteById(customerId)).thenReturn(true);
        mockMvc.perform(post("/customers/delete/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("success"))
                .andExpect(view().name("edit-customer"));
    }

    @Test
    void testDeleteCustomer_DataAccessException() throws Exception {
        Mockito.doThrow(new DataIntegrityViolationException("DB error")).when(customerService).deleteById(customerId);
        mockMvc.perform(post("/customers/delete/{id}", customerId))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(view().name("edit-customer"));
    }
    @Test
    void testChangeCustomerById_Success() throws Exception {
        Mockito.when(customerService.findById(customerId)).thenReturn(Optional.of(customer));
        Mockito.when(accountService.findByCustomerId(customerId)).thenReturn(Optional.ofNullable(accounts));
        Mockito.when(cEService.findEmployersByCustomerId(customerId)).thenReturn(employers);

        mockMvc.perform(get("/customers/change")
                        .param("id", String.valueOf(customerId)))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-customer"))
                .andExpect(model().attributeExists("customer", "accounts", "employers"));
    }
    @Test
    void testChangeCustomerById_NotFound() throws Exception {
        Mockito.when(customerService.findById(customerId)).thenReturn(Optional.empty());
        mockMvc.perform(get("/customers/change")
                        .param("id", String.valueOf(customerId)))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-customer"));
    }





    @Test
    void testListCustomers() throws Exception {
        int page = 1;
        int size = 10;
        List<Customer> customerList = List.of(customer);
        Page<Customer> customerPage = new PageImpl<>(customerList, PageRequest.of(0, size), customerList.size());
        Mockito.when(customerService.findAll(any(Pageable.class))).thenReturn(customerPage);
        CustomerResponse mockCustomerResponse = new CustomerResponse();
        mockCustomerResponse.setId(customerId);
        mockCustomerResponse.setName(customer.getName());
        mockCustomerResponse.setSurname(customer.getSurname());
        mockCustomerResponse.setEmail(customer.getEmail());
        mockCustomerResponse.setPhoneNumber(customer.getPhoneNumber());
        Mockito.when(customerFacade.getCustomerResponseByCustomer(any())).thenReturn(mockCustomerResponse);
        mockMvc.perform(get("/customers/all").param("page", String.valueOf(page)).param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(view().name("customers"))
                .andExpect(model().attributeExists("customers"))
                .andExpect(model().attribute("currentPage", page))
                .andExpect(model().attribute("totalPages", 1))
                .andExpect(model().attribute("totalCustomers", (long) customerList.size()));
    }


    @Test
    void testCustomerWithoutDetails() throws Exception {
        mockMvc.perform(get("/customers/change")
                        .param("searchId", (String) null))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-customer"))
                .andExpect(model().attributeDoesNotExist("customer"));
    }
    @Test
    void testAddToEmployer_ValidRequest_ShouldRedirect() throws Exception {
        Customer_EmployerRequest request = new Customer_EmployerRequest();
        request.setEmployerName("Some Employer");
        request.setCustomerId(customerId);
        Mockito.when(employerService.findByEmployerName(request.getEmployerName()))
                .thenReturn(Optional.of(new Employer( request.getEmployerName(),"Some Address")));
        Mockito.when(customerService.findById(request.getCustomerId()))
                .thenReturn(Optional.of(customer));
        Mockito.when(cEService.findEmployerByCustomerIdAndEmployerId(1L, 1L))
                .thenReturn(Optional.empty());
        mockMvc.perform(post("/customers/add_to_employer")
                        .param("customerId", "1")
                        .param("employerName", "SomeEmployer"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/change?id=1"));
    }

    @Test
    void testFindCustomerById_CustomerExists_ShouldReturnSearchPage() throws Exception {
        CustomerResponse customerResponse = customerFacade.getCustomerResponseByCustomer(customer);
        Mockito.when(customerService.findById(1L)).thenReturn(Optional.of(customer));
        Mockito.when(customerFacade.getCustomerResponseByCustomer(customer)).thenReturn(customerResponse);
        mockMvc.perform(get("/customers/find").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customer"))
                .andExpect(view().name("search"));
    }

    @Test
    void testUpdateCustomer_Success_ShouldRedirect() throws Exception {
        CustomerRequest customerRequest = new CustomerRequest();
        customerRequest.setId(customerId);
        Mockito.when(customerService.save(any())).thenReturn(true);
        mockMvc.perform(post("/customers/update/1")
                        .param("name", "Updated Name"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/customers/change?id=1"));
    }
    @Test
    void testErrorPage_ShouldReturnErrorView() throws Exception {
        mockMvc.perform(get("/customers/error"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }
}
