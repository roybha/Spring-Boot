package com.example.SpringWeb.service;

import com.example.SpringWeb.model.Admin;
import com.example.SpringWeb.model.AdminPrincipal;
import com.example.SpringWeb.repository.AdminRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
class AdminServiceIntegrationTest {

        @Autowired
        private AdminService adminService;

        @Autowired
        private AdminRepository adminRepository;

        private Admin testAdmin;

        @BeforeEach
        void setUp() {
            adminRepository.deleteAll();

            testAdmin = new Admin();
            testAdmin.setUsername("admin123");
            testAdmin.setPassword("password123");
            testAdmin = adminRepository.save(testAdmin);
        }

        @Test
        void testSaveAdmin() {
            Admin newAdmin = new Admin();
            newAdmin.setUsername("newAdmin");
            newAdmin.setPassword("newPassword");
            adminService.saveAdmin(newAdmin);
            assertTrue(adminRepository.findByUsername("newAdmin").isPresent());
        }

        @Test
        void testLoadUserByUsername() {
            AdminPrincipal foundAdmin = (AdminPrincipal) adminService.loadUserByUsername(testAdmin.getUsername());
            assertEquals(testAdmin.getUsername(), foundAdmin.getUsername());
        }

        @Test
        void testFindByUsername() {
            Optional<Admin> foundAdmin = adminRepository.findByUsername(testAdmin.getUsername());
            assertTrue(foundAdmin.isPresent());
            assertEquals(testAdmin.getUsername(), foundAdmin.get().getUsername());
        }
}

