package com.example.SpringWeb.service;

import com.example.SpringWeb.DAO.DAO;
import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Currency;
import com.example.SpringWeb.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AccountService implements DAO<Account> {
    private final AccountRepository accountRepository;
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public AccountService(JdbcTemplate jdbcTemplate, AccountRepository accountRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountRepository = accountRepository;
    }
    @Override
    public boolean save(Account account) {
        try {
            accountRepository.save(account);
            return true;
        } catch (DataAccessException e) {
            e.printStackTrace();
            return false;
        }
    }


    @Override
    public boolean delete(Account account) {
        try {
            accountRepository.delete(account);
            return true;
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
        return accountRepository.findAll();
    }

    @Override
    public boolean deleteById(long id) {
        try {
            accountRepository.deleteById(id);
            return true;
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public Optional<Account> findById(long id) {
        return accountRepository.findById(id);
    }


    private RowMapper<Account> AccountRowMapper(){
        return (rs, rowNum) -> {
            Account account = new Account();
            account.setId(rs.getLong("id"));
            account.setAccountNumber(rs.getString("account_number"));
            account.setCurrency(Currency.getFromName(rs.getString("currency")));
            account.setBalance(rs.getDouble("balance"));
            //account.setCustomer(rs.getLong(("customer_id")));
          return account;
        };
    }
    public Optional<List<Account>> findByCustomerId(long customerId) {
        try {
            List<Account> accounts = accountRepository.findByCustomerId(customerId);
            return Optional.of(accounts);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public Optional<Account> findByAccountNumber(String accountNumber) {
        try {
            return accountRepository.findByAccountNumber(accountNumber);
        } catch (DataAccessException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    public static String generateAccountNumber() {
        // Використовуємо випадкове значення для генерування номера
        return "ACC" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);  // Форматуємо як ACCXXXXXX, де X - це цифри
    }
}
