package com.example.SpringWeb.service;

import com.example.SpringWeb.DAO.CustomerDAO;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService implements CustomerDAO {
    private final CustomerRepository customerRepository;
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public CustomerService(JdbcTemplate jdbcTemplate, CustomerRepository customerRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRepository = customerRepository;

    }
    private long getNewCustomerID() {
        String query = "SELECT MAX(id) FROM customers";
        Long lastId = jdbcTemplate.queryForObject(query, Long.class);
        // Якщо lastId null (немає записів в таблиці), то повертаємо 1 як новий ID
        return (lastId != null) ? lastId + 1 : 1;
    }


    @Override
    public boolean save(Customer customer) {
        try {
            customerRepository.save(customer);
            return true;
        } catch (DataAccessException e) {  // Використовуємо DataAccessException, оскільки це більш загальний виняток для JdbcTemplate
            return false;  // Повертаємо false, якщо сталася помилка
        }
    }


    @Override
    public boolean delete(Customer customer) {

        try {
            // Перевіряємо, чи існує клієнт з таким ID
            Optional<Customer> existingCustomer = customerRepository.findById(customer.getId());

            // Якщо клієнт існує, виконуємо видалення
            if (existingCustomer.isPresent()) {
                customerRepository.deleteById(customer.getId());
                return true;  // Успішно видалено
            } else {
                return false;  // Клієнт не знайдений, видалення не відбулося
            }
        } catch (Exception e) {
            // Логування помилки можна додати, якщо потрібно
            return false;
        }
    }


    @Override
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> findById(long id) {
       return customerRepository.findById(id);
    }

    @Override
    public void deleteAll(List<Customer> t) {
        String sql = "DELETE FROM customers WHERE id = ? AND name =? AND age =? AND surname =? AND email =?";
        //customers.forEach(customer -> jdbcTemplate.update(sql, customer.getId(), customer.getName(), customer.getAge(), customer.getSurname(), customer.getEmail()));
    }

    @Override
    public boolean deleteById(long id) {
        try {
            // Перевіряємо, чи існує клієнт з таким ID
            Optional<Customer> customer = customerRepository.findById(id);

            // Якщо клієнт існує, виконуємо видалення
            if (customer.isPresent()) {
                customerRepository.deleteById(id);  // Викликаємо deleteById з JpaRepository
                return true;  // Успішно видалено
            } else {
                return false;  // Клієнт не знайдений, видалення не відбулося
            }
        } catch (Exception e) {
            // Логування помилки (опційно)
            return false;  // Повертаємо false, якщо сталася помилка
        }
    }
    // RowMapper для перетворення рядка з БД у об'єкт Customer
    private RowMapper<Customer> customerRowMapper() {
        return (rs, rowNum) -> {
            Customer customer = new Customer();
            customer.setId(rs.getLong("id"));
            customer.setName(rs.getString("name"));
            customer.setSurname(rs.getString("surname"));
            customer.setEmail(rs.getString("email"));
            customer.setAge(rs.getInt("age"));
            return customer;
        };
    }
}
