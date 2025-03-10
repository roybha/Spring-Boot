package com.example.SpringWeb.repository;
import com.example.SpringWeb.model.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    void  deleteById(long id);
    Optional<Customer> findById(Long id);
    List<Customer> findAll();
    Page<Customer> findAll(Pageable pageable);
}
