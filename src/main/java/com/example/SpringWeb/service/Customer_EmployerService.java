package com.example.SpringWeb.service;

import com.example.SpringWeb.DAO.Customer_EmployerDAO;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.model.Customer_Employer;
import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.repository.Customer_EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class Customer_EmployerService implements Customer_EmployerDAO {
    private final Customer_EmployerRepository customerEmployerRepository;
    @Autowired
    public Customer_EmployerService(Customer_EmployerRepository customerEmployerRepository) {
        this.customerEmployerRepository = customerEmployerRepository;
    }
    @Override
    public boolean save(Customer_Employer customerEmployer) {
        try {
            customerEmployerRepository.save(customerEmployer);
            return true;
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public boolean delete(Customer_Employer customerEmployer) {
        try {
            Optional<Customer_Employer> maybeCustomerEmployer = customerEmployerRepository.findById(customerEmployer.getId());
            if (maybeCustomerEmployer.isPresent()) {
                customerEmployerRepository.delete(maybeCustomerEmployer.get());
                return true;
            }
            else
                return false;
        }catch (DataAccessException e){
            return false;
        }

    }

    @Override
    public void deleteAll(List<Customer_Employer> t) {

    }

    @Override
    public List<Customer_Employer> findAll() {
        return customerEmployerRepository.findAll();
    }

    @Override
    public boolean deleteById(long id) {
        try {
            Optional<Customer_Employer> maybeCustomerEmployer = customerEmployerRepository.findById(id);
            if (maybeCustomerEmployer.isPresent()) {
                customerEmployerRepository.delete(maybeCustomerEmployer.get());
                return true;
            }
            return false;
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public Optional<Customer_Employer> findById(long id) {
        try {
            return customerEmployerRepository.findById(id);
        }catch (DataAccessException e){
            return Optional.empty();
        }
    }
    public List<Customer> findCustomersByEmployerId(Long employerId) {
        List<Customer_Employer> customerEmployers = customerEmployerRepository.findByEmployerId(employerId);
        List<Customer> customers = new ArrayList<>();
        for (Customer_Employer customerEmployer : customerEmployers) {
            customers.add(customerEmployer.getCustomer());
        }
        return customers;
    }
    public List<Employer> findEmployersByCustomerId(Long customerId) {
        List<Customer_Employer> customerEmployers = customerEmployerRepository.findByCustomerId(customerId);
        List<Employer> employers = new ArrayList<>();
        for (Customer_Employer customerEmployer : customerEmployers) {
            employers.add(customerEmployer.getEmployer());
        }
        return employers;
    }
    public void deleteByCustomerId(Long customerId) {
        List<Customer_Employer> customerEmployers = customerEmployerRepository.findByCustomerId(customerId);
        customerEmployerRepository.deleteAll(customerEmployers);
    }
    public void deleteCustomerFromEmployer(Long customerId, Long employerId) {
       Optional<Customer_Employer> maybeCustomerEmployer = Optional.of(customerEmployerRepository.findByCustomerIdAndEmployerId(customerId,employerId));
       maybeCustomerEmployer.ifPresent(customerEmployerRepository::delete);
    }
    public Optional<Customer_Employer> findEmployerByCustomerIdAndEmployerId(Long customerId, Long employerId) {
        return Optional.ofNullable(customerEmployerRepository.findByCustomerIdAndEmployerId(customerId,employerId));
    }

}
