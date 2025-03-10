package com.example.SpringWeb.controller;
import com.example.SpringWeb.DTO.Customer_EmployerRequest;
import com.example.SpringWeb.DTO.EmployerRequest;
import com.example.SpringWeb.DTO.EmployerResponse;
import com.example.SpringWeb.facade.EmployerFacade;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.service.CustomerService;
import com.example.SpringWeb.service.Customer_EmployerService;
import com.example.SpringWeb.service.EmployerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.resource.servlet.OAuth2ResourceServerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("deprecation")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class,
        UserDetailsServiceAutoConfiguration.class, OAuth2ClientAutoConfiguration.class,
        OAuth2ResourceServerAutoConfiguration.class})
class EmployerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EmployerService employerService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private Customer_EmployerService cEService;

    @Autowired
    private EmployerFacade employerFacade;



    private Employer employer;
    private Long employerId;

    @TestConfiguration
    static class TestConfig {
        @Bean(name = "employerService")
        public EmployerService employerService() {
            return mock(EmployerService.class);
        }

        @Bean(name = "employerFacade")
        public EmployerFacade employerFacade() {
            return mock(EmployerFacade.class);
        }
        @Bean(name = "customerService")
        public CustomerService customerService() {
            return mock(CustomerService.class);
        }
        @Bean(name = "cEService")
        @Primary
        public Customer_EmployerService cEService() {
            return mock(Customer_EmployerService.class);
        }
        @Bean
        public WebSecurityCustomizer webSecurityCustomizer() {
            return (web) -> web.ignoring().requestMatchers("/**");
        }
    }

    @BeforeEach
    void setUp()  {
        employer = new Employer("Some Employer", "Some Address");
        employer.setId(1L);
        employer.setCustomers(new ArrayList<>());
        employerId = employer.getId();
        Mockito.when(employerService.findById(employerId)).thenReturn(Optional.of(employer));
        Mockito.when(employerService.save(any())).thenReturn(true);
    }




    @Test
    void testCreateEmployer_Success() throws Exception {
        mockMvc.perform(post("/employers/addEmployer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", employer.getName())
                        .param("address", employer.getAddress()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employers/create"));
    }

    @Test
    void testCreateEmployer_UnSuccess() throws Exception {
        mockMvc.perform(post("/employers/addEmployer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", employer.getName())
                        .param("address", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employers/create"))
                .andExpect(flash().attributeExists("error"));
    }


    @Test
    void testDeleteEmployer_NotFound() throws Exception {
        Mockito.when(employerService.findByEmployerName(Mockito.any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/employers/deleteEmployer/{name}", employer.getName() + "122"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("error"))
                .andExpect(flash().attribute("error", "Не знайдено компанію з іменем: Some Employer122"))
                .andExpect(redirectedUrl("/employers/change"));
    }



    @Test
    void testDeleteEmployer_Success() throws Exception {
        Mockito.when(employerService.findByEmployerName(employer.getName())).thenReturn(Optional.of(employer));
        mockMvc.perform(post("/employers/deleteEmployer/{name}", employer.getName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("success"))
                .andExpect(flash().attribute("success","Компанію успішно видалено"))
                .andExpect(redirectedUrl("/employers/change"));
    }


    @Test
    void testChangeEmployerById_NotFound() throws Exception {
        Mockito.when(employerService.findById(employerId)).thenReturn(Optional.empty());
        mockMvc.perform(get("/employers/change")
                        .param("id", String.valueOf(employerId)))
                .andExpect(status().isOk())
                .andExpect(view().name("edit-employer"));
    }
    @Test
    void testAddCustomerToEmployer_Success() throws Exception {
        Customer_EmployerRequest request = new Customer_EmployerRequest();
        request.setEmployerName("Some Employer");
        request.setCustomerId(1L);
        Employer employer = new Employer("Some Employer", "Some Address");
        employer.setId(1L);
        Customer customer = new Customer("Some Customer", "Some Email", "some@gmail.com", 30, "somePass12", "+380958546311", new ArrayList<>(), new ArrayList<>());
        customer.setId(1L);
        Mockito.when(employerService.findByEmployerName(request.getEmployerName())).thenReturn(Optional.of(employer));
        Mockito.when(customerService.findById(request.getCustomerId())).thenReturn(Optional.of(customer));
        Mockito.when(cEService.findCustomersByEmployerId(employer.getId())).thenReturn(new ArrayList<>());
        mockMvc.perform(post("/employers/addCustomer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("employerName", request.getEmployerName())
                        .param("customerId", String.valueOf(request.getCustomerId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/employers/change?employerName=Some Employer*"))
                .andExpect(model().attribute("success", "Клієнта успішно додано до компанії"));
    }


    @Test
    void testAddCustomerToEmployer_EmployerNotFound() throws Exception {
        Customer_EmployerRequest request = new Customer_EmployerRequest();
        request.setEmployerName("Nonexistent Employer");
        request.setCustomerId(1L);
        Mockito.when(employerService.findByEmployerName(request.getEmployerName())).thenReturn(Optional.empty());
        mockMvc.perform(post("/employers/addCustomer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("employerName", request.getEmployerName())
                        .param("customerId", String.valueOf(request.getCustomerId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/employers/change?employerName=Nonexistent Employer*"))
                .andExpect(model().attribute("error", "Не знайдено компанію з ім'ям Nonexistent Employer"));
    }
    @Test
    void testAddCustomerToEmployer_CustomerNotFound() throws Exception {
        Customer_EmployerRequest request = new Customer_EmployerRequest();
        request.setEmployerName("Some Employer");
        request.setCustomerId(1L);
        Employer employer = new Employer("Some Employer", "Some Address");
        employer.setId(1L);
        Mockito.when(employerService.findByEmployerName(request.getEmployerName())).thenReturn(Optional.of(employer));
        Mockito.when(customerService.findById(request.getCustomerId())).thenReturn(Optional.empty());

        mockMvc.perform(post("/employers/addCustomer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("employerName", request.getEmployerName())
                        .param("customerId", String.valueOf(request.getCustomerId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/employers/change?employerName=Some Employer*"))
                .andExpect(model().attribute("error", "Не знайдено клієнта з Id: 1"));
    }
    @Test
    void testUpdateEmployer_Success() throws Exception {
        EmployerRequest employerRequest = new EmployerRequest();
        employerRequest.setName("Updated Employer");
        employerRequest.setAddress("Updated Address");

        Employer employer = new Employer("Some Employer", "Some Address");
        employer.setId(1L);
        Mockito.when(employerService.findById(1L)).thenReturn(Optional.of(employer));
        mockMvc.perform(post("/employers/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", employerRequest.getName())
                        .param("address", employerRequest.getAddress()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employers/change?employerName=Updated Employer"))
                .andExpect(flash().attribute("success", "Компанію успішно оновлено!"));
    }

    @Test
    void testUpdateEmployer_NotFound() throws Exception {
        EmployerRequest employerRequest = new EmployerRequest();
        employerRequest.setName("Updated Employer");
        employerRequest.setAddress("Updated Address");
        Mockito.when(employerService.findById(1L)).thenReturn(Optional.empty());
        mockMvc.perform(post("/employers/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", employerRequest.getName())
                        .param("address", employerRequest.getAddress()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/employers/change?employerName=Updated Employer"))
                .andExpect(flash().attribute("error", "Компанію з таким ID не знайдено."));
    }
    @Test
    void testFindEmployer_Success() throws Exception {
        Customer_EmployerRequest request = new Customer_EmployerRequest();
        request.setEmployerName("Some Employer");



        Mockito.when(employerService.findByEmployerName(request.getEmployerName())).thenReturn(Optional.of(employer));
        EmployerResponse employerResponse = new EmployerResponse();
        employerResponse.setName(employer.getName());
        employerResponse.setAddress(employer.getAddress());
        employerResponse.setCustomers(new ArrayList<>());
        Mockito.when(employerFacade.getEmployerResponseByEmployer(employer)).thenReturn(employerResponse);
        mockMvc.perform(get("/employers/find")
                        .param("employerName", request.getEmployerName()))
                .andExpect(status().isOk())
                .andExpect(view().name("search-employer"))
                .andExpect(model().attribute("employer", employerResponse))
                .andExpect(model().attribute("customers", new ArrayList<>()));
    }

    @Test
    void testFindEmployer_NotFound() throws Exception {
        Customer_EmployerRequest request = new Customer_EmployerRequest();
        request.setEmployerName("Nonexistent Employer");
        Mockito.when(employerService.findByEmployerName(request.getEmployerName())).thenReturn(Optional.empty());
        mockMvc.perform(get("/employers/find")
                        .param("employerName", request.getEmployerName()))
                .andExpect(status().isOk())
                .andExpect(view().name("search-employer"))
                .andExpect(model().attribute("error", "Не знайдено компанію з назвою: Nonexistent Employer"));
    }
    @Test
    void testDeleteCustomer_Success() throws Exception {
        Customer_EmployerRequest request = new Customer_EmployerRequest();
        request.setEmployerName("Some Employer");
        request.setCustomerId(1L);
        Employer employer = new Employer("Some Employer", "Some Address");
        employer.setId(1L);
        Customer customer = new Customer("Some Customer", "Some Email","some@gmail.com",33,"somePass122","0956227321",new ArrayList<>(),new ArrayList<>());
        customer.setId(1L);
        Mockito.when(employerService.findByEmployerName(request.getEmployerName())).thenReturn(Optional.of(employer));
        Mockito.when(customerService.findById(request.getCustomerId())).thenReturn(Optional.of(customer));
        mockMvc.perform(post("/employers/deleteCustomer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("employerName", request.getEmployerName())
                        .param("customerId", String.valueOf(request.getCustomerId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/employers/change?employerName=Some Employer*"))
                .andExpect(model().attribute("success", "Клієнта з Id 1 успішно видаленно з компанії  Some Employer"));
    }

    @Test
    void testDeleteCustomer_EmployerNotFound() throws Exception {
        Customer_EmployerRequest request = new Customer_EmployerRequest();
        request.setEmployerName("Nonexistent Employer");
        request.setCustomerId(1L);
        Mockito.when(employerService.findByEmployerName(request.getEmployerName())).thenReturn(Optional.empty());
        mockMvc.perform(post("/employers/deleteCustomer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("employerName", request.getEmployerName())
                        .param("customerId", String.valueOf(request.getCustomerId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/employers/change?employerName=Nonexistent Employer*"))
                .andExpect(model().attribute("error", "Не знайдено компанію з іменем: Nonexistent Employer"));
    }

    @Test
    void testDeleteCustomer_CustomerNotFound() throws Exception {
        Customer_EmployerRequest request = new Customer_EmployerRequest();
        request.setEmployerName("Some Employer");
        request.setCustomerId(1L);
        Employer employer = new Employer("Some Employer", "Some Address");
        employer.setId(1L);
        Mockito.when(employerService.findByEmployerName(request.getEmployerName())).thenReturn(Optional.of(employer));
        Mockito.when(customerService.findById(request.getCustomerId())).thenReturn(Optional.empty());

        mockMvc.perform(post("/employers/deleteCustomer")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("employerName", request.getEmployerName())
                        .param("customerId", String.valueOf(request.getCustomerId())))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/employers/change?employerName=Some Employer*"))
                .andExpect(model().attribute("error", "Не знайдено клієнта з Id: 1"));
    }
    @Test
    void testAllEmployers() throws Exception {
        List<Employer> employers = List.of(
                new Employer("Employer 1", "Address 1"),
                new Employer("Employer 2", "Address 2")
        );
        Page<Employer> employerPage = new PageImpl<>(employers);
        Mockito.when(employerService.findAll(Mockito.any(Pageable.class))).thenReturn(employerPage);
        mockMvc.perform(get("/employers/all")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(view().name("employers"))
                .andExpect(model().attribute("employers", employers.stream()
                        .map(employerFacade::getEmployerResponseByEmployer)
                        .collect(Collectors.toList())))
                .andExpect(model().attribute("currentPage", 1))
                .andExpect(model().attribute("totalPages", employerPage.getTotalPages()))
                .andExpect(model().attribute("totalEmployers", employerPage.getTotalElements()))
                .andExpect(model().attribute("size", 10));
    }




}
