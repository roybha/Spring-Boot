package com.example.SpringWeb.service;

import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.model.Customer_Employer;
import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.repository.Customer_EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Customer_EmployerServiceTest {

    private Customer_EmployerService customerEmployerService;
    private Customer_EmployerRepository customerEmployerRepository;

    @BeforeEach
    void setUp() {
        customerEmployerRepository = Mockito.mock(Customer_EmployerRepository.class);
        customerEmployerService = new Customer_EmployerService(customerEmployerRepository);
    }

    @Test
    void testSave() {
        Customer_Employer customerEmployer = new Customer_Employer();
        when(customerEmployerRepository.save(customerEmployer)).thenReturn(customerEmployer);
        boolean result = customerEmployerService.save(customerEmployer);
        assertTrue(result);
        verify(customerEmployerRepository, times(1)).save(customerEmployer);
    }

    @Test
    void testSaveFails() {
        Customer_Employer customerEmployer = new Customer_Employer();
        when(customerEmployerRepository.save(customerEmployer)).thenThrow(new DataIntegrityViolationException("DB Error"));
        boolean result = customerEmployerService.save(customerEmployer);
        assertFalse(result);
        verify(customerEmployerRepository, times(1)).save(customerEmployer);
    }

    @Test
    void testDelete() {
        Customer_Employer customerEmployer = new Customer_Employer();
        when(customerEmployerRepository.findById(customerEmployer.getId())).thenReturn(Optional.of(customerEmployer));
        boolean result = customerEmployerService.delete(customerEmployer);
        assertTrue(result);
        verify(customerEmployerRepository, times(1)).delete(customerEmployer);
    }

    @Test
    void testDeleteNotFound() {
        Customer_Employer customerEmployer = new Customer_Employer();
        when(customerEmployerRepository.findById(customerEmployer.getId())).thenReturn(Optional.empty());
        boolean result = customerEmployerService.delete(customerEmployer);
        assertFalse(result);
        verify(customerEmployerRepository, times(0)).delete(customerEmployer);
    }

    @Test
    void testDeleteById() {
        long id = 1L;
        Customer_Employer customerEmployer = new Customer_Employer();
        when(customerEmployerRepository.findById(id)).thenReturn(Optional.of(customerEmployer));
        boolean result = customerEmployerService.deleteById(id);
        assertTrue(result);
        verify(customerEmployerRepository, times(1)).delete(customerEmployer);
    }

    @Test
    void testDeleteByIdNotFound() {
        long id = 1L;
        when(customerEmployerRepository.findById(id)).thenReturn(Optional.empty());
        boolean result = customerEmployerService.deleteById(id);
        assertFalse(result);
        verify(customerEmployerRepository, times(0)).delete(any());
    }

    @Test
    void testFindById() {
        long id = 1L;
        Customer_Employer customerEmployer = new Customer_Employer();
        when(customerEmployerRepository.findById(id)).thenReturn(Optional.of(customerEmployer));
        Optional<Customer_Employer> result = customerEmployerService.findById(id);
        assertTrue(result.isPresent());
        assertEquals(customerEmployer, result.get());
    }

    @Test
    void testFindByIdNotFound() {
        long id = 1L;
        when(customerEmployerRepository.findById(id)).thenReturn(Optional.empty());
        Optional<Customer_Employer> result = customerEmployerService.findById(id);
        assertFalse(result.isPresent());
    }

    @Test
    void testFindCustomersByEmployerId() {
        Long employerId = 1L;
        List<Customer_Employer> customerEmployerList = new ArrayList<>();
        Customer_Employer customerEmployer = new Customer_Employer();
        Customer customer = new Customer();
        customerEmployer.setCustomer(customer);
        customerEmployerList.add(customerEmployer);
        when(customerEmployerRepository.findByEmployerId(employerId)).thenReturn(customerEmployerList);
        List<Customer> customers = customerEmployerService.findCustomersByEmployerId(employerId);
        assertEquals(1, customers.size());
        assertEquals(customer, customers.get(0));
    }

    @Test
    void testFindEmployersByCustomerId() {
        Long customerId = 1L;
        List<Customer_Employer> customerEmployerList = new ArrayList<>();
        Customer_Employer customerEmployer = new Customer_Employer();
        Employer employer = new Employer();
        customerEmployer.setEmployer(employer);
        customerEmployerList.add(customerEmployer);
        when(customerEmployerRepository.findByCustomerId(customerId)).thenReturn(customerEmployerList);
        List<Employer> employers = customerEmployerService.findEmployersByCustomerId(customerId);
        assertEquals(1, employers.size());
        assertEquals(employer, employers.get(0));
    }

    @Test
    void testDeleteCustomerFromEmployer() {
        Long customerId = 1L;
        Long employerId = 1L;
        Customer_Employer customerEmployer = new Customer_Employer();
        when(customerEmployerRepository.findByCustomerIdAndEmployerId(customerId, employerId)).thenReturn(customerEmployer);
        customerEmployerService.deleteCustomerFromEmployer(customerId, employerId);
        verify(customerEmployerRepository, times(1)).delete(customerEmployer);
    }

    @Test
    void testDeleteCustomerFromEmployerNotFound() {
        Long customerId = 1L;
        Long employerId = 1L;
        when(customerEmployerRepository.findByCustomerIdAndEmployerId(customerId, employerId)).thenReturn(null);
        customerEmployerService.deleteCustomerFromEmployer(customerId, employerId);
        verify(customerEmployerRepository, times(0)).delete(any());
    }


    @Test
    void testDeleteByCustomerId() {
        Long customerId = 1L;
        List<Customer_Employer> customerEmployerList = new ArrayList<>();
        Customer_Employer customerEmployer = new Customer_Employer();
        customerEmployerList.add(customerEmployer);
        when(customerEmployerRepository.findByCustomerId(customerId)).thenReturn(customerEmployerList);
        customerEmployerService.deleteByCustomerId(customerId);
        verify(customerEmployerRepository, times(1)).deleteAll(customerEmployerList);
    }
}
