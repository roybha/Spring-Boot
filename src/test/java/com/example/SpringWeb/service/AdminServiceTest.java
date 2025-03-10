package com.example.SpringWeb.service;
import com.example.SpringWeb.model.Admin;
import com.example.SpringWeb.model.AdminPrincipal;
import com.example.SpringWeb.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminService adminService;

    private Admin admin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        admin = new Admin();
        admin.setId(1L);
        admin.setUsername("admin");
        admin.setPassword("password123");
    }

    @Test
    void testSaveAdmin() {
        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
        when(adminRepository.save(admin)).thenReturn(admin);
        adminService.saveAdmin(admin);
        verify(passwordEncoder, times(1)).encode("password123");
        verify(adminRepository, times(1)).save(admin);
        assertEquals("encodedPassword", admin.getPassword());
    }


    @Test
    void testLoadUserByUsername_UserFound() {
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.of(admin));
        AdminPrincipal principal = (AdminPrincipal) adminService.loadUserByUsername("admin");
        assertNotNull(principal);
        assertEquals("admin", principal.getUsername());
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        when(adminRepository.findByUsername("admin")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> adminService.loadUserByUsername("admin"));
    }
}
