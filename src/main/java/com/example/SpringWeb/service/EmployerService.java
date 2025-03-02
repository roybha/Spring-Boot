package com.example.SpringWeb.service;

import com.example.SpringWeb.DAO.EmployerDAO;
import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.repository.EmployerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
@Transactional
public class EmployerService implements EmployerDAO {
    private final EmployerRepository employerRepository;
    @Autowired
    public EmployerService(EmployerRepository employerRepository) {
        this.employerRepository = employerRepository;
    }

    @Override
    public boolean save(Employer employer) {
        try {
            employerRepository.save(employer);
            return true;
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public boolean delete(Employer employer) {
        try {
            Optional<Employer> maybeEmployer = employerRepository.findById(employer.getId());
            if (maybeEmployer.isPresent()) {
                employerRepository.delete(maybeEmployer.get());
                return true;
            }else {
                return false;
            }
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public void deleteAll(List<Employer> t) {

    }

    @Override
    public List<Employer> findAll() {
        return employerRepository.findAll();
    }

    @Override
    public boolean deleteById(long id) {
        try {
            Optional<Employer> maybeEmployer = employerRepository.findById(id);
            if (maybeEmployer.isPresent()) {
                employerRepository.delete(maybeEmployer.get());
                return true;
            }
            return false;
        }catch (DataAccessException e){
            return false;
        }
    }

    @Override
    public Optional<Employer> findById(long id) {
       return employerRepository.findById(id);
    }
    public Optional<Employer> findByNameAndAddress(String name,String address) {
        return employerRepository.findByNameAndAddress(name, address);
    }
    public Optional<Employer> findByEmployerName(String employerName) {
        return employerRepository.findByName(employerName);
    }
    public Page<Employer> findAll(Pageable pageable) {
        return employerRepository.findAll(pageable);
    }
}
