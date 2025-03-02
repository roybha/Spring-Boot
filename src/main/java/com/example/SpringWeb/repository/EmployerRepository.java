package com.example.SpringWeb.repository;

import com.example.SpringWeb.model.Employer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
    Optional<Employer> findById(Long id);
    Optional<Employer> findByNameAndAddress(String email, String address);
    Optional<Employer>findByName(String name);
    Page<Employer> findAll(Pageable pageable);
}
