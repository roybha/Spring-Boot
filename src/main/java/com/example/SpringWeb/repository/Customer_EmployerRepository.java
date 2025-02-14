package com.example.SpringWeb.repository;
import com.example.SpringWeb.model.Customer_Employer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Customer_EmployerRepository extends JpaRepository<Customer_Employer, Long> {
    List<Customer_Employer> findByCustomerId(Long customerId);
    List<Customer_Employer> findByEmployerId(Long employerId);
    Customer_Employer findByCustomerIdAndEmployerId(Long customerId, Long employerId);
}
