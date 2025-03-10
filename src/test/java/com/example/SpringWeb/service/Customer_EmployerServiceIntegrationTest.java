package com.example.SpringWeb.service;

import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.model.Customer_Employer;
import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.repository.Customer_EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
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
class Customer_EmployerServiceIntegrationTest {

    @Autowired
    private Customer_EmployerService customerEmployerService;

    @Autowired
    private Customer_EmployerRepository customerEmployerRepository;

    private Customer testCustomer;
    private Employer testEmployer;
    private Customer_Employer testCustomerEmployer;

    @BeforeEach
    void setUp() {
        customerEmployerRepository.deleteAll();
        testCustomer = new Customer( "Some Name", "Some Surname", "email@gmail.com", 19,"somePass12223","095327719",new ArrayList<>(), new ArrayList<>());
        testCustomer.setId(1L);
        testEmployer = new Employer("Test Name", "Test Address", List.of(testCustomer));
        testEmployer.setId(2L);
        testCustomerEmployer = new Customer_Employer();
        testCustomerEmployer.setCustomer(testCustomer);
        testCustomerEmployer.setEmployer(testEmployer);
        testCustomerEmployer = customerEmployerRepository.save(testCustomerEmployer);
    }

    @Test
    void testSave() {
        Customer newCustomer = new Customer("New Name", "New Surname", "newemail@gmail.com", 30, "newPass12345", "095327722", new ArrayList<>(), new ArrayList<>());
        newCustomer.setId(2L);
        Employer newEmployer = new Employer("New Employer", "New Address", List.of(newCustomer));
        newEmployer.setId(3L);
        Customer_Employer newCustomerEmployer = new Customer_Employer();
        newCustomerEmployer.setCustomer(newCustomer);
        newCustomerEmployer.setEmployer(newEmployer);
        boolean isSaved = customerEmployerService.save(newCustomerEmployer);
        assertTrue(isSaved);
        assertTrue(customerEmployerRepository.findById(newCustomerEmployer.getId()).isPresent());
    }


    @Test
    void testDelete() {
        boolean isDeleted = customerEmployerService.delete(testCustomerEmployer);
        assertTrue(isDeleted);
        assertFalse(customerEmployerRepository.findById(testCustomerEmployer.getId()).isPresent());
    }

    @Test
    void testFindAll() {
        List<Customer_Employer> customerEmployers = customerEmployerService.findAll();
        assertEquals(1, customerEmployers.size());
        assertTrue(customerEmployers.contains(testCustomerEmployer));
    }

    @Test
    void testFindById() {
        Optional<Customer_Employer> foundCustomerEmployer = customerEmployerService.findById(testCustomerEmployer.getId());
        assertTrue(foundCustomerEmployer.isPresent());
        assertEquals(testCustomerEmployer.getId(), foundCustomerEmployer.get().getId());
    }

    @Test
    void testDeleteById() {
        boolean isDeleted = customerEmployerService.deleteById(testCustomerEmployer.getId());
        assertTrue(isDeleted);
        assertFalse(customerEmployerRepository.findById(testCustomerEmployer.getId()).isPresent());
    }

    @Test
    void testFindCustomersByEmployerId() {
        List<Customer> customers = customerEmployerService.findCustomersByEmployerId(testEmployer.getId());
        assertEquals(1, customers.size());
        assertTrue(customers.contains(testCustomer));
    }

    @Test
    void testFindEmployersByCustomerId() {
        List<Employer> employers = customerEmployerService.findEmployersByCustomerId(testCustomer.getId());
        assertEquals(1, employers.size());
        assertTrue(employers.contains(testEmployer));
    }

    @Test
    void testDeleteByCustomerId() {
        customerEmployerService.deleteByCustomerId(testCustomer.getId());
        List<Customer_Employer> customerEmployers = customerEmployerService.findAll();
        assertTrue(customerEmployers.isEmpty());
    }

    @Test
    void testDeleteCustomerFromEmployer() {
        customerEmployerService.deleteCustomerFromEmployer(testCustomer.getId(), testEmployer.getId());
        List<Customer_Employer> customerEmployers = customerEmployerService.findAll();
        assertTrue(customerEmployers.isEmpty());
    }

    @Test
    void testFindEmployerByCustomerIdAndEmployerId() {
        Optional<Customer_Employer> foundCustomerEmployer = customerEmployerService.findEmployerByCustomerIdAndEmployerId(testCustomer.getId(), testEmployer.getId());
        assertTrue(foundCustomerEmployer.isPresent());
        assertEquals(testCustomerEmployer.getId(), foundCustomerEmployer.get().getId());
    }
}
