package com.example.SpringWeb.service;

import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.repository.EmployerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class EmployerServiceTest {
    @Mock
    private EmployerRepository employerRepository;

    @InjectMocks
    private EmployerService employerService;

    private Employer employer;

    @BeforeEach
    void setUp() {
        employer = new Employer("Some Co", "some address");
        employer.setId(1L);
    }

    @Test
    void saveShouldReturnTrueWhenEmployerIsSavedSuccessfully() {
        when(employerRepository.save(employer)).thenReturn(employer);
        boolean result = employerService.save(employer);
        assertTrue(result);
    }

    @Test
    void saveShouldReturnFalseWhenExceptionOccurs() {
        when(employerRepository.save(employer)).thenThrow(new DataIntegrityViolationException("Database error"));
        boolean result = employerService.save(employer);
        assertFalse(result);
    }

    @Test
    void deleteShouldReturnTrueWhenEmployerIsDeletedSuccessfully() {
        when(employerRepository.findById(employer.getId())).thenReturn(Optional.of(employer));
        doNothing().when(employerRepository).delete(employer);
        boolean result = employerService.delete(employer);
        assertTrue(result);
    }

    @Test
    void deleteShouldReturnFalseWhenEmployerNotFound() {
        when(employerRepository.findById(employer.getId())).thenReturn(Optional.empty());
        boolean result = employerService.delete(employer);
        assertFalse(result);
    }

    @Test
    void deleteByIdShouldReturnTrueWhenEmployerIsDeletedSuccessfully() {
        when(employerRepository.findById(employer.getId())).thenReturn(Optional.of(employer));
        doNothing().when(employerRepository).delete(employer);
        boolean result = employerService.deleteById(employer.getId());
        assertTrue(result);
    }

    @Test
    void deleteByIdShouldReturnFalseWhenEmployerNotFound() {
        when(employerRepository.findById(employer.getId())).thenReturn(Optional.empty());
        boolean result = employerService.deleteById(employer.getId());
        assertFalse(result);
    }

    @Test
    void findByIdShouldReturnEmployerWhenFound() {
        when(employerRepository.findById(employer.getId())).thenReturn(Optional.of(employer));
        Optional<Employer> result = employerService.findById(employer.getId());
        assertTrue(result.isPresent());
        assertEquals(employer, result.get());
    }

    @Test
    void findByIdShouldReturnEmptyWhenNotFound() {
        when(employerRepository.findById(employer.getId())).thenReturn(Optional.empty());
        Optional<Employer> result = employerService.findById(employer.getId());
        assertFalse(result.isPresent());
    }

    @Test
    void findByNameAndAddressShouldReturnEmployerWhenFound() {
        when(employerRepository.findByNameAndAddress("Some Co", "some address")).thenReturn(Optional.of(employer));
        Optional<Employer> result = employerService.findByNameAndAddress("Some Co", "some address");
        assertTrue(result.isPresent());
        assertEquals(employer, result.get());
    }

    @Test
    void findByNameAndAddressShouldReturnEmptyWhenNotFound() {
        when(employerRepository.findByNameAndAddress("Some Co", "some address")).thenReturn(Optional.empty());
        Optional<Employer> result = employerService.findByNameAndAddress("Some Co", "some address");
        assertFalse(result.isPresent());
    }
}
