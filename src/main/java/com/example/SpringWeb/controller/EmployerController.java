package com.example.SpringWeb.controller;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.model.Customer_Employer;
import com.example.SpringWeb.model.Employer;
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
@RequestMapping("/employers")
public class EmployerController {
    private final EmployerService employerService;
    private final Customer_EmployerService ceService;
    private final CustomerService customerService;
    @Autowired
    public EmployerController(EmployerService employerService, Customer_EmployerService ceService, CustomerService customerService) {
        this.employerService = employerService;
        this.ceService = ceService;
        this.customerService = customerService;
    }
    @PostMapping("/addEmployer")
    public String addEmployer(
                              @RequestParam(name = "name") String name,
                              @RequestParam(name = "address") String address,
                              RedirectAttributes redirectAttributes) {
        try {
            Optional<Employer> maybeExist = employerService.findByNameAndAddress(name, address);
            if (maybeExist.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Компанія вже інсує.");
            }else{
               Employer newEmployer = new Employer(name, address);
                employerService.save(newEmployer);
                redirectAttributes.addFlashAttribute("success", "Компанія додана успішно");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        return "redirect:/employers/create";
    }
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        return "create-employer";
    }
    @PostMapping("/addCustomer")
    public String addCustomerToEmployer(@RequestParam(name = "employerName") String employerName,
                                        @RequestParam(name = "customerId") String customerId,
                                        RedirectAttributes redirectAttributes) {
        try {
            if( employerName != null && !employerName.isEmpty()) {
                Optional<Employer> maybeEmployer = employerService.findByEmployerName(employerName);
                if(maybeEmployer.isPresent()){
                    Long ceId = Long.parseLong(customerId);
                    if(ceId != null && ceId > 0) {
                        Optional<Customer> maybeCustomer = customerService.findById(ceId);
                        if (maybeCustomer.isPresent()) {
                            if(!ceService.findCustomersByEmployerId(maybeEmployer.get().getId()).isEmpty()) {
                                redirectAttributes.addFlashAttribute("error","Клієнт вже присутній у даній компанії");
                            }
                            else {
                                redirectAttributes.addFlashAttribute("success","Клієнта успішно додано до компанії");
                                ceService.save(new Customer_Employer(maybeEmployer.get(), maybeCustomer.get()));
                            }
                        }
                        else
                            redirectAttributes.addFlashAttribute("error","Не знайдено клієнта з Id: "+ ceId);
                    }
                }
            }
            else
                redirectAttributes.addFlashAttribute("error","Не знайдено компанію з ім'ям: "+ employerName);

        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","помилка доступу до БД");
        }
        return "redirect:/employers/change?name=" + employerName;
    }
    @GetMapping("/change")
    public String showChangeForm(@RequestParam(name = "name",required = false)String employerName,Model model) {
        try {
            if(employerName != null) {
                Optional<Employer> maybeEmployer = employerService.findByEmployerName(employerName);
                if(maybeEmployer.isPresent()) {
                    model.addAttribute("employer", maybeEmployer.get());
                    Optional<List<Customer>> maybeCustomers = Optional.of(ceService.findCustomersByEmployerId(maybeEmployer.get().getId()));
                    maybeCustomers.ifPresent(customers -> model.addAttribute("customers", customers));
                }
                else
                    model.addAttribute("error","Не знайдено компанію з  назвою: "+ employerName);
            }
        }catch (Exception e) {
            model.addAttribute("error","Помилка доступу до БД");
        }
        return "edit-employer";
    }
    @PostMapping("/deleteEmployer/{name}")
    public String deleteEmployer(@PathVariable(name = "name") String employerName, RedirectAttributes redirectAttributes) {
        try {
            Optional<Employer> maybeEmployer = employerService.findByEmployerName(employerName);
            if(maybeEmployer.isPresent()) {
                employerService.delete(maybeEmployer.get());
                redirectAttributes.addFlashAttribute("success","Компанію успішно видалено");
            }else
                redirectAttributes.addFlashAttribute("error","Не знайдено компанію з іменем: "+ employerName);
        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до БД");
        }
        return "redirect:/employers/change";
    }
    @PostMapping("/deleteCustomer")
    public String deleteCustomer(@RequestParam(name = "customerId") Long customerId,
                                 @RequestParam(name = "employerName") String employerName,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<Employer> maybeEmployer = employerService.findByEmployerName(employerName);
            if(maybeEmployer.isPresent()) {
                Optional<Customer> maybeCustomer = customerService.findById(customerId);
                if(maybeCustomer.isPresent()) {
                    ceService.deleteByCustomerId(maybeCustomer.get().getId());
                    redirectAttributes.addFlashAttribute("success","Клієнта з Id "+customerId +" успішно видаленно з компанії  "+employerName);
                }
                else{
                    redirectAttributes.addFlashAttribute("error","Не знайдено клієнта з Id: "+ customerId);
                }
            }
            else
                redirectAttributes.addFlashAttribute("error","Не знайдено компанію з іменем: "+ employerName);
        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до БД");
        }
        return "redirect:/employers/change?name=" + employerName;
    }
    @GetMapping("/find")
    public String findEmployer(@RequestParam(name = "employerName",required = false) String employerName,
                               Model model) {
        if(employerName != null) {
            Optional<Employer> maybeEmployer = employerService.findByEmployerName(employerName);
            if(maybeEmployer.isPresent()) {
                 model.addAttribute("employer", maybeEmployer.get());
                 Optional<List<Customer>>maybeCustomers = Optional.of(ceService.findCustomersByEmployerId(maybeEmployer.get().getId()));
                 maybeCustomers.ifPresent(customers -> model.addAttribute("customers", customers));
            }
            else {
                model.addAttribute("error","Не знайдено компанію з назвою: "+ employerName);
            }
        }
        return "search-employer";
    }
    @GetMapping("/all")
    public String allEmployers(Model model) {
        model.addAttribute("employers", employerService.findAll());
        return "employers";
    }

}
