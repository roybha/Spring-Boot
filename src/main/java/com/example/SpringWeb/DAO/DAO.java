package com.example.SpringWeb.DAO;

import java.util.List;
import java.util.Optional;

public interface DAO <T>{
    public boolean save(T t);
    public boolean delete(T t);
    public void deleteAll(List<T> t);
    public List<T> findAll();
    public boolean deleteById(long id);
    public Optional<T> findById(long id);
}
