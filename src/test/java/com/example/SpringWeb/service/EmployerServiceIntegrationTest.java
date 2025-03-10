package com.example.SpringWeb.service;

import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.repository.EmployerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
public class EmployerServiceIntegrationTest {

    @Autowired
    private EmployerService employerService;

    @Autowired
    private EmployerRepository employerRepository;

    @Test
    public void testSaveEmployer() {
        Employer employer = new Employer();
        employer.setName("Test Employer");
        employer.setAddress("Test Address");

        boolean result = employerService.save(employer);
        assertTrue(result);

        Optional<Employer> savedEmployer = employerRepository.findByNameAndAddress("Test Employer", "Test Address");
        assertTrue(savedEmployer.isPresent());
    }

    @Test
    public void testFindById() {
        Employer employer = new Employer();
        employer.setName("FindMe");
        employer.setAddress("Find Address");
        employerRepository.save(employer);

        Optional<Employer> foundEmployer = employerService.findById(employer.getId());
        assertTrue(foundEmployer.isPresent());
        assertEquals("FindMe", foundEmployer.get().getName());
    }

    @Test
    public void testFindByNameAndAddress() {
        Employer employer = new Employer();
        employer.setName("Unique Name");
        employer.setAddress("Unique Address");
        employerService.save(employer);

        Optional<Employer> foundEmployer = employerService.findByNameAndAddress("Unique Name", "Unique Address");
        assertTrue(foundEmployer.isPresent());
        assertEquals("Unique Name", foundEmployer.get().getName());
    }

    @Test
    public void testFindByEmployerName() {
        Employer employer = new Employer();
        employer.setName("Search Name");
        employer.setAddress("Some Address");
        employerService.save(employer);

        Optional<Employer> foundEmployer = employerService.findByEmployerName("Search Name");
        assertTrue(foundEmployer.isPresent());
        assertEquals("Search Name", foundEmployer.get().getName());
    }

    @Test
    public void testFindAll() {
        Employer employer1 = new Employer();
        employer1.setName("Employer 1");
        employer1.setAddress("Address 1");
        employerService.save(employer1);

        Employer employer2 = new Employer();
        employer2.setName("Employer 2");
        employer2.setAddress("Address 2");
        employerService.save(employer2);

        List<Employer> employers = employerService.findAll();
        assertFalse(employers.isEmpty());
        assertEquals(17, employers.size());
    }

    @Test
    public void testFindAllWithPagination() {
        for (int i = 1; i <= 5; i++) {
            Employer employer = new Employer();
            employer.setName("Employer " + i);
            employer.setAddress("Address " + i);
            employerService.save(employer);
        }

        Pageable pageable = PageRequest.of(0, 3);
        Page<Employer> employerPage = employerService.findAll(pageable);

        assertNotNull(employerPage);
        assertEquals(3, employerPage.getContent().size());
    }

    @Test
    public void testDeleteEmployer() {
        Employer employer = new Employer();
        employer.setName("ToDelete");
        employer.setAddress("Delete Address");
        employerService.save(employer);
        Optional<Employer> savedEmployer = employerRepository.findByNameAndAddress("ToDelete", "Delete Address");
        assertTrue(savedEmployer.isPresent());
        boolean deleteResult = employerService.delete(savedEmployer.get());
        assertTrue(deleteResult);
        Optional<Employer> deletedEmployer = employerRepository.findByNameAndAddress("ToDelete", "Delete Address");
        assertFalse(deletedEmployer.isPresent());
    }

    @Test
    public void testDeleteById() {
        Employer employer = new Employer();
        employer.setName("DeleteById");
        employer.setAddress("Some Address");
        employerService.save(employer);
        Optional<Employer> savedEmployer = employerRepository.findByNameAndAddress("DeleteById", "Some Address");
        assertTrue(savedEmployer.isPresent());
        boolean deleteResult = employerService.deleteById(savedEmployer.get().getId());
        assertTrue(deleteResult);
        Optional<Employer> deletedEmployer = employerRepository.findById(savedEmployer.get().getId());
        assertFalse(deletedEmployer.isPresent());
    }

    @Test
    public void testDeleteNonExistentEmployer() {
        Employer employer = new Employer();
        employer.setId(999L);
        boolean result = employerService.delete(employer);
        assertFalse(result);
    }

    @Test
    public void testDeleteByIdNonExistent() {
        boolean result = employerService.deleteById(999L);
        assertFalse(result);
    }

    @Test
    public void testSaveEmployerWithExceptionHandling() {
        try {
            boolean result = employerService.save(null);
            assertFalse(result);
        } catch (DataAccessException | NullPointerException e) {
            assertTrue(true);
        }
    }
}
