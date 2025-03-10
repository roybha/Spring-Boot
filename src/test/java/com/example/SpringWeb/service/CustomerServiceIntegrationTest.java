package com.example.SpringWeb.service;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.repository.CustomerRepository;
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
class CustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer testCustomer;

    @BeforeEach
    void setUp() {
        customerRepository.deleteAll();
        testCustomer = new Customer( "Andriy", "Dolynskyi", "andriy.dol@gmail.com", 30,"somePass12","+380958963711",new ArrayList<>(),new ArrayList<>());
        testCustomer = customerRepository.save(testCustomer);
    }

    @Test
    void testSave() {
        Customer newCustomer = new Customer();
        newCustomer.setName("Jane");
        newCustomer.setSurname("Smith");
        newCustomer.setEmail("jane.smith@email.com");
        newCustomer.setPassword("password123");
        newCustomer.setPhoneNumber("+380958963712");
        newCustomer.setAge(28);
        newCustomer.setAccounts(new ArrayList<>());
        newCustomer.setEmployers(new ArrayList<>());

        boolean isSaved = customerService.save(newCustomer);
        assertTrue(isSaved);
        assertTrue(customerRepository.findById(newCustomer.getId()).isPresent());
    }

    @Test
    void testDelete() {
        boolean isDeleted = customerService.delete(testCustomer);
        assertTrue(isDeleted);
        assertFalse(customerRepository.findById(testCustomer.getId()).isPresent());
    }

    @Test
    void testFindAll() {
        List<Customer> customers = customerService.findAll();
        assertEquals(1, customers.size());
        assertTrue(customers.contains(testCustomer));
    }

    @Test
    void testFindById() {
        Optional<Customer> foundCustomer = customerService.findById(testCustomer.getId());
        assertTrue(foundCustomer.isPresent());
        assertEquals(testCustomer.getId(), foundCustomer.get().getId());
    }

    @Test
    void testDeleteById() {
        boolean isDeleted = customerService.deleteById(testCustomer.getId());
        assertTrue(isDeleted);
        assertFalse(customerRepository.findById(testCustomer.getId()).isPresent());
    }

    @Test
    void testFindAllWithPagination() {
        customerRepository.save(new Customer( "Alice", "Brown", "alice.brown@email.com", 25,"somePass112234","+380953448672",new ArrayList<>(), new ArrayList<>()));
        customerRepository.save(new Customer("Vladyslav", "Freymut", "vladfr.fr@gmail.com", 35, "somePass1223232","0958932782",new ArrayList<>(), new ArrayList<>()));
        List<Customer> customers = customerService.findAll();
        assertEquals(3, customers.size());
    }
}

