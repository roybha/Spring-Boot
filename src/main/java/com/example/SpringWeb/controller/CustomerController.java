package com.example.SpringWeb.controller;

import com.example.SpringWeb.model.Account;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.model.Customer_Employer;
import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.service.AccountService;
import com.example.SpringWeb.service.CustomerService;
import com.example.SpringWeb.service.Customer_EmployerService;
import com.example.SpringWeb.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/customers")
public class CustomerController {
    private final CustomerService customerService;
    private final AccountService accountService;
    private final Customer_EmployerService customerEmployerService;
    private final EmployerService employerService;
    @GetMapping("/error")
    public String errorPage(Model model) {
        return "error";
    }
    @Autowired
    public CustomerController(CustomerService customerService, AccountService accountService,
                              Customer_EmployerService customerEmployerService, EmployerService employerService) {
        this.accountService = accountService;
        this.customerService = customerService;
        this.customerEmployerService = customerEmployerService;
        this.employerService = employerService;
    }

    @GetMapping("/all")
    public String showCustomerPage(Model model) {
        List<Customer> customers = customerService.findAll();
        model.addAttribute("customers", customers);
        return "customers"; // Без .jsp
    }
    @PostMapping("/add")
    public String addCustomer(@RequestParam String name,
                              @RequestParam String surname,
                              @RequestParam String email,
                              @RequestParam int age,
                              RedirectAttributes redirectAttributes) {
        try {
            if(name.isEmpty() || surname.isEmpty() || email.isEmpty() || age < 0) {
                redirectAttributes.addFlashAttribute("error", "Не всі поля для створення клієнта заповнені");
            }else{
                // Створення нового клієнта
                Customer customer = new Customer();
                customer.setName(name);
                customer.setSurname(surname);
                customer.setEmail(email);
                customer.setAge(age);
                customerService.save(customer);
                redirectAttributes.addFlashAttribute("success", "Клієнта додано успішно");
            }
        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до бази даних");
        }
        return "redirect:/customers/create";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "create";
    }
    @GetMapping("/find")
    public String findCustomerById(@RequestParam(name = "id",required = false) Long id, Model model) {
        try {
            if(id != null){
                Optional<Customer> customer = customerService.findById(id);
                if(customer.isPresent()){
                    model.addAttribute("customer", customer.get());
                    Optional<List<Account>> accounts = accountService.findByCustomerId(customer.get().getId());
                    accounts.ifPresent(accountList -> model.addAttribute("accounts", accountList));
                    Optional<List<Employer>> employers = Optional.of(customerEmployerService.findEmployersByCustomerId(id));
                    employers.ifPresent(employerList -> model.addAttribute("employers", employerList));
                }
                else
                    model.addAttribute("error","Не знайдено відповідного користувача");
            }

        }catch (DataAccessException e){
            model.addAttribute("error","Помилка доступу до БД");
        }
        return "search";

    }
    @GetMapping("/change")
    public String changeCustomerById(@RequestParam(name = "id",required = false) Long id,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        try {
            if(id != null) {
                Optional<Customer> customer = customerService.findById(id);
                if (customer.isPresent()) {
                    Optional<List<Account>> customerAccounts = accountService.findByCustomerId(id);
                    customerAccounts.ifPresent(x -> customer.get().setAccounts(x));
                    model.addAttribute("customer", customer.get());
                    model.addAttribute("accounts", customer.get().getAccounts());
                    Optional<List<Employer>> employers = Optional.of(customerEmployerService.findEmployersByCustomerId(id));
                    employers.ifPresent(employerList -> model.addAttribute("employers", employerList));
                    return "edit-customer";
                }
            }
        }catch (DataAccessException e){
                model.addAttribute("error", "Клієнта з таким ID не знайдено");
        }

        return "edit-customer";
    }
    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable int id, @ModelAttribute Customer customer, Model model) {
        boolean updated = customerService.save(customer);
        if (updated) {
            model.addAttribute("success", "Дані клієнта успішно оновлено");
        } else {
            model.addAttribute("error", "Не вдалося оновити клієнта");
        }
        return "redirect:/customers/change?id=" + id;
    }
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable int id, Model model) {
        try {
            if(customerService.deleteById(id)) {
                model.addAttribute("success","Клієнта з ID " + id + " успішно видалено.");
            }else {
                model.addAttribute("error","Не вдалося знайти клієнта з ID " + id + " для видалення.");
            }
        }catch (DataAccessException e){
            model.addAttribute("error","Не вдалося видалити клієнта з "+id);
        }
        return "edit-customer";
    }
    @PostMapping("/remove_from_employer/{customerId}")
    public String removeFromEmployer(@PathVariable Long customerId,
                                     @RequestParam Long employerId,
                                     RedirectAttributes redirectAttributes) {
        try {
            Optional<Employer> employer = employerService.findById(employerId);
            if(employer.isPresent()){
                customerEmployerService.deleteCustomerFromEmployer(customerId, employerId);
                redirectAttributes.addFlashAttribute("success","Клієнта з Id "+customerId+" успішно видалено з компанії");
            }
            else
                redirectAttributes.addFlashAttribute("error","Не знайдено компанію з Id "+employerId);

        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до БД");
        }
        return "redirect:/customers/change?id=" + customerId;
    }
    @PostMapping("/add_to_employer")
    public String addToEmployer(@RequestParam(name = "customerId") Long customerId,
                                @RequestParam(name = "employerName") String employerName,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<Employer> maybeEmployer = employerService.findByEmployerName(employerName);
            if(maybeEmployer.isPresent()){
                Optional<Customer_Employer> maybeCE = customerEmployerService.findEmployerByCustomerIdAndEmployerId(customerId, maybeEmployer.get().getId());
                if(maybeCE.isPresent()){
                    redirectAttributes.addFlashAttribute("error","Відповідний контракт між користувачем і компанією вже підпписаний");
                }else {
                    Optional<Customer> customerOpt = customerService.findById(customerId);
                    if (customerOpt.isPresent()) {
                        Customer customer = customerOpt.get();
                        Employer employer = maybeEmployer.get();

                        Customer_Employer customerEmployer = new Customer_Employer(employer,customer);
                        customerEmployerService.save(customerEmployer);
                    }else{
                        redirectAttributes.addFlashAttribute("error","Не знайдено клієнта з Id "+ customerId);
                    }
                }
            }
            else
                redirectAttributes.addFlashAttribute("error","Не знайдено компанію з ім'ям "+employerName);


        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до БД");
        }
        return "redirect:/customers/change?id=" + customerId;
    }

}
