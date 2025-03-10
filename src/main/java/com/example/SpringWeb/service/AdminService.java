package com.example.SpringWeb.service;
import com.example.SpringWeb.model.Admin;
import com.example.SpringWeb.model.AdminPrincipal;
import com.example.SpringWeb.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class AdminService implements UserDetailsService {
    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public void saveAdmin(Admin user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        adminRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Admin> admin = adminRepository.findByUsername(username);
        if (admin.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }
        return new AdminPrincipal(admin.get());
    }
}
