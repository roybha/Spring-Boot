package com.example.SpringWeb.service;

import com.example.SpringWeb.DAO.CustomerDAO;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Repository
public class CustomerService implements CustomerDAO {
    private List<Customer> customers;
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public CustomerService(JdbcTemplate jdbcTemplate) {
        this.customers =  new ArrayList<>();
        this.jdbcTemplate = jdbcTemplate;

    }
    private long getNewCustomerID() {
        String query = "SELECT MAX(id) FROM customers";
        Long lastId = jdbcTemplate.queryForObject(query, Long.class);
        // Якщо lastId null (немає записів в таблиці), то повертаємо 1 як новий ID
        return (lastId != null) ? lastId + 1 : 1;
    }


    @Override
    public boolean save(Customer customer) {
        // Перевірка, чи існує клієнт з таким ID (або іншою унікальною характеристикою, наприклад, email)
        String searchQuery = "SELECT COUNT(*) FROM customers WHERE id=?";
        Long count = jdbcTemplate.queryForObject(searchQuery, Long.class, customer.getId());

        try {
            // Якщо клієнт існує, виконуємо оновлення
            if (count != null && count > 0) {
                String updateQuery = "UPDATE customers SET name=?, surname=?, email=?, age=? WHERE id=?";
                jdbcTemplate.update(updateQuery, customer.getName(), customer.getSurname(), customer.getEmail(), customer.getAge(), customer.getId());
                return true;  // Оновлено успішно
            } else {
                // Якщо клієнт не існує, виконуємо вставку нового запису
                String insertQuery = "INSERT INTO customers (name, surname, email, age) VALUES (?, ?, ?, ?)";
                jdbcTemplate.update(insertQuery, customer.getName(), customer.getSurname(), customer.getEmail(), customer.getAge());
                return true;  // Новий запис додано успішно
            }
        } catch (DataAccessException e) {  // Використовуємо DataAccessException, оскільки це більш загальний виняток для JdbcTemplate
            return false;  // Повертаємо false, якщо сталася помилка
        }
    }


    @Override
    public boolean delete(Customer customer) {
        String sql = "DELETE FROM customers WHERE id = ? AND name = ? AND surname = ? AND email = ? AND age = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, customer.getId(), customer.getName(), customer.getSurname(), customer.getEmail(), customer.getAge());
            // Якщо було видалено хоча б один рядок, то повертаємо true
            return rowsAffected > 0;
        } catch (Exception e) {
            // Логування помилки можна додати, якщо потрібно
            return false;
        }
    }


    @Override
    public List<Customer> findAll() {
        String sql = "SELECT * FROM customers";
        return jdbcTemplate.query(sql, customerRowMapper());
    }

    @Override
    public Optional<Customer> findById(long id) {
        String sql = "SELECT * FROM customers WHERE id = ?";
        Customer customer = jdbcTemplate.queryForObject(sql, customerRowMapper(), id);
        return Optional.ofNullable(customer);
    }

    @Override
    public void deleteAll(List<Customer> t) {
        String sql = "DELETE FROM customers WHERE id = ? AND name =? AND age =? AND surname =? AND email =?";
        customers.forEach(customer -> jdbcTemplate.update(sql, customer.getId(), customer.getName(), customer.getAge(), customer.getSurname(), customer.getEmail()));
    }

    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM customers WHERE id = ?";
        try {
            int rowsAffected = jdbcTemplate.update(sql, id);
            // Якщо було видалено хоча б один рядок, то повертаємо true
            return rowsAffected > 0;
        } catch (Exception e) {
            // Логування помилки можна додати, якщо потрібно
            return false;
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
