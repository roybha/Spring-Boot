package com.example.SpringWeb.service;

import com.example.SpringWeb.DAO.DAO;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Repository
public class AccountService implements DAO<Account> {
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public AccountService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public boolean save(Account account) {
        String checkQuery = "SELECT COUNT(*) FROM accounts WHERE id = ?";
        String insertQuery = "INSERT INTO accounts (account_number, balance, currency, customer_id) VALUES (?,?,?::currency_type,?)";
        String updateQuery = "UPDATE accounts SET balance = ?, currency = ?::currency_type, customer_id = ? WHERE id = ?";

        try {
            int count = jdbcTemplate.queryForObject(checkQuery, Integer.class, account.getId());

            if (count > 0) {
                jdbcTemplate.update(updateQuery, account.getBalance(), account.getCurrency().toString(), account.getCustomer(), account.getId());
            } else {
                jdbcTemplate.update(insertQuery, generateAccountNumber(), account.getBalance(), account.getCurrency().toString(), account.getCustomer());
            }
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean delete(Account account) {
        String sql = "DELETE FROM accounts WHERE id = ? AND account_number = ? AND balance = ? AND currency = ? AND customer_id = ?";
        try {
            int affectedRows = jdbcTemplate.update(sql,account.getId(),account.getAccountNumber(),account.getBalance(),account.getCurrency().toString(),account.getId());
            return affectedRows > 0;
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public void deleteAll(List<Account> t) {
        String sql = "DELETE FROM accounts WHERE id = ? AND account_number = ? AND balance = ? AND currency = ? AND customer_id = ?";
        t.forEach(account -> jdbcTemplate.update(sql,account.getId(),account.getAccountNumber(),account.getBalance(),account.getCurrency(),account.getId()));
    }

    @Override
    public List<Account> findAll() {
        String sql = "SELECT * FROM accounts";
        return jdbcTemplate.query(sql,AccountRowMapper());
    }

    @Override
    public boolean deleteById(long id) {
        String searchQuery = "DELETE FROM accounts WHERE id = ?";
        try {
            int affectedRows = jdbcTemplate.update(searchQuery,id);
            return affectedRows > 0;
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public Optional<Account> findById(long id) {
       String sql = "SELECT * FROM accounts WHERE id = ?";
       Account account = jdbcTemplate.queryForObject(sql,AccountRowMapper(),id);
       return Optional.ofNullable(account);
    }
    private RowMapper<Account> AccountRowMapper(){
        return (rs, rowNum) -> {
            Account account = new Account();
            account.setId(rs.getLong("id"));
            account.setAccountNumber(rs.getString("account_number"));
            account.setCurrency(Currency.getFromName(rs.getString("currency")));
            account.setBalance(rs.getDouble("balance"));
            account.setCustomer(rs.getLong(("customer_id")));
          return account;
        };
    }
    public Optional<List<Account>> findByCustomerId(long customerId) {
        String sql = "SELECT * FROM accounts WHERE customer_id = ?";
        Optional<List<Account>> customersAccounts = Optional.of(jdbcTemplate.query(sql,AccountRowMapper(),customerId));
        return customersAccounts;
    }
    public Optional<Account> findByAccountNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        Account account = jdbcTemplate.queryForObject(sql,AccountRowMapper(),accountNumber);
        return Optional.ofNullable(account);
    }
    public static String generateAccountNumber() {
        // Використовуємо випадкове значення для генерування номера
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);  // Форматуємо як ACCXXXXXX, де X - це цифри
    }
}
