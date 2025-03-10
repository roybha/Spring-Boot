package com.example.SpringWeb.service;

import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer("John", "Doe", "john.doe@email.com", 30);
        customer.setId(12L);
    }

    @Test
    void saveShouldReturnTrueWhenCustomerIsSavedSuccessfully() {
        when(customerRepository.save(customer)).thenReturn(customer);
        boolean result = customerService.save(customer);
        assertTrue(result);
    }

    @Test
    void saveShouldReturnFalseWhenExceptionOccurs() {
        when(customerRepository.save(customer)).thenThrow(new DataAccessException("Database error") {});
        boolean result = customerService.save(customer);
        assertFalse(result);
    }

    @Test
    void deleteShouldReturnTrueWhenCustomerIsDeletedSuccessfully() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(customer.getId());
        boolean result = customerService.delete(customer);
        assertTrue(result);
        verify(customerRepository, times(1)).deleteById(customer.getId());
    }

    @Test
    void deleteShouldReturnFalseWhenCustomerDoesNotExist() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());
        boolean result = customerService.delete(customer);
        assertFalse(result);
    }

    @Test
    void deleteByIdShouldReturnTrueWhenCustomerIsDeletedSuccessfully() {
        when(customerRepository.findById(anyLong())).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(anyLong());
        boolean result = customerService.deleteById(customer.getId());
        assertTrue(result);
    }


    @Test
    void deleteByIdShouldReturnFalseWhenCustomerDoesNotExist() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());
        boolean result = customerService.deleteById(customer.getId());
        assertFalse(result);
    }

    @Test
    void findByIdShouldReturnCustomerWhenCustomerExists() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        Optional<Customer> result = customerService.findById(customer.getId());
        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenCustomerDoesNotExist() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.empty());
        Optional<Customer> result = customerService.findById(customer.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void findAllShouldReturnPageOfCustomers() {
        Pageable pageable = Pageable.unpaged();
        Page<Customer> page = mock(Page.class);
        when(customerRepository.findAll(pageable)).thenReturn(page);
        Page<Customer> result = customerService.findAll(pageable);
        assertEquals(page, result);
    }
}
