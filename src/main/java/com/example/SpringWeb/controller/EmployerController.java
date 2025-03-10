package com.example.SpringWeb.controller;
import com.example.SpringWeb.DTO.CustomerResponse;
import com.example.SpringWeb.DTO.Customer_EmployerRequest;
import com.example.SpringWeb.DTO.EmployerRequest;
import com.example.SpringWeb.DTO.EmployerResponse;
import com.example.SpringWeb.facade.CustomerFacade;
import com.example.SpringWeb.facade.EmployerFacade;
import com.example.SpringWeb.model.Customer;
import com.example.SpringWeb.model.Customer_Employer;
import com.example.SpringWeb.model.Employer;
import com.example.SpringWeb.service.CustomerService;
import com.example.SpringWeb.service.Customer_EmployerService;
import com.example.SpringWeb.service.EmployerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/employers")
public class EmployerController {
    private final EmployerService employerService;
    private final Customer_EmployerService ceService;
    private final CustomerService customerService;
    private final EmployerFacade employerFacade;
    private final CustomerFacade customerFacade;
    @Autowired
    public EmployerController(EmployerService employerService, Customer_EmployerService ceService,
                              CustomerService customerService,EmployerFacade employerFacade,CustomerFacade customerFacade) {
        this.employerService = employerService;
        this.ceService = ceService;
        this.customerService = customerService;
        this.employerFacade = employerFacade;
        this.customerFacade = customerFacade;
    }
    @PostMapping("/addEmployer")
    public String addEmployer(
            @ModelAttribute @Validated EmployerRequest employerRequest,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        try {
            if (bindingResult.hasErrors()) {
                redirectAttributes.addFlashAttribute("error", bindingResult.getFieldError().getDefaultMessage());
                return "redirect:/employers/create";
            }
            Optional<Employer> maybeExist = employerService.findByNameAndAddress(employerRequest.getName(), employerRequest.getAddress());
            if (maybeExist.isPresent()) {
                redirectAttributes.addFlashAttribute("error", "Компанія вже інсує.");
            }else{
               Employer newEmployer = new Employer(employerRequest.getName(), employerRequest.getAddress());
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
    public String addCustomerToEmployer(@ModelAttribute @Validated Customer_EmployerRequest customerEmployerRequest,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        try {
            if(!bindingResult.hasErrors() ) {
                Optional<Employer> maybeEmployer = employerService.findByEmployerName(customerEmployerRequest.getEmployerName());
                if(maybeEmployer.isPresent()){
                        Optional<Customer> maybeCustomer = customerService.findById(customerEmployerRequest.getCustomerId());
                        if (maybeCustomer.isPresent()) {
                            if (ceService.findCustomersByEmployerId(maybeEmployer.get().getId())
                                    .stream()
                                    .anyMatch(customer -> customer.getId().equals(customerEmployerRequest.getCustomerId()))) {
                                redirectAttributes.addAttribute("error","Клієнт вже присутній у даній компанії");
                            }
                            else {
                                redirectAttributes.addAttribute("success","Клієнта успішно додано до компанії");
                                ceService.save(new Customer_Employer(maybeEmployer.get(), maybeCustomer.get()));
                            }
                        }
                        else
                            redirectAttributes.addAttribute("error","Не знайдено клієнта з Id: "+ customerEmployerRequest.getCustomerId());
                }
                else{
                    redirectAttributes.addAttribute("error","Не знайдено компанію з ім'ям "+customerEmployerRequest.getEmployerName());
                }
            }
            else
                redirectAttributes.addAttribute("error",bindingResult.getFieldError().getDefaultMessage());

        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","помилка доступу до БД");
        }
        return "redirect:/employers/change?employerName=" + customerEmployerRequest.getEmployerName();
    }
    @GetMapping("/change")
    public String showChangeForm(@ModelAttribute Customer_EmployerRequest customerEmployerRequest,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            if(customerEmployerRequest.getEmployerName() != null) {
                Optional<Employer> maybeEmployer = employerService.findByEmployerName(customerEmployerRequest.getEmployerName());

                if(maybeEmployer.isPresent()) {
                    model.addAttribute("employer", employerFacade.getEmployerResponseByEmployer(maybeEmployer.get()));
                    List<Customer> customers = ceService.findCustomersByEmployerId(maybeEmployer.get().getId());
                    List<CustomerResponse> customerResponses = customers.stream()
                            .map(customerFacade::getCustomerResponseByCustomer)
                            .collect(Collectors.toList());

                    model.addAttribute("customers", customerResponses);
                }
                else
                    model.addAttribute("error","Не знайдено компанію з  назвою: "+ customerEmployerRequest.getEmployerName());
            }
        }catch (Exception e) {
            model.addAttribute("error","Помилка доступу до БД");
        }
        return "edit-employer";
    }
    @PostMapping("/update/{id}")
    public String updateEmployer(@PathVariable Long id,
                                 @ModelAttribute EmployerRequest employerRequest,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<Employer> maybeEmployer = employerService.findById(id);

            if (maybeEmployer.isPresent()) {
                Employer employer = maybeEmployer.get();
                if(bindingResult.hasFieldErrors("name")) {
                    redirectAttributes.addAttribute("error",bindingResult.getFieldError().getDefaultMessage());
                    return "redirect:/employers/change?employerName=" + employer.getName();
                }
                employer.setName(employerRequest.getName());
               if(bindingResult.hasFieldErrors("address")) {
                   redirectAttributes.addFlashAttribute("error",bindingResult.getFieldError().getDefaultMessage());
                   return "redirect:/employers/change?employerName=" + employer.getName();
               }
                employer.setAddress(employerRequest.getAddress());

                employerService.save(employer);
                redirectAttributes.addFlashAttribute("success", "Компанію успішно оновлено!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Компанію з таким ID не знайдено.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Помилка при оновленні компанії.");
        }

        return "redirect:/employers/change?employerName=" + employerRequest.getName();
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
    public String deleteCustomer(@ModelAttribute Customer_EmployerRequest customerEmployerRequest,
                                 RedirectAttributes redirectAttributes) {
        try {
            Optional<Employer> maybeEmployer = employerService.findByEmployerName(customerEmployerRequest.getEmployerName());
            if(maybeEmployer.isPresent()) {
                Optional<Customer> maybeCustomer = customerService.findById(customerEmployerRequest.getCustomerId());
                if(maybeCustomer.isPresent()) {
                    ceService.deleteByCustomerId(maybeCustomer.get().getId());
                    redirectAttributes.addAttribute("success","Клієнта з Id "+customerEmployerRequest.getCustomerId() +" успішно видаленно з компанії  "+customerEmployerRequest.getEmployerName());
                }
                else{
                    redirectAttributes.addAttribute("error","Не знайдено клієнта з Id: "+ customerEmployerRequest.getCustomerId());
                }
            }
            else
                redirectAttributes.addAttribute("error","Не знайдено компанію з іменем: "+ customerEmployerRequest.getEmployerName());
        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до БД");
        }
        return "redirect:/employers/change?employerName=" + customerEmployerRequest.getEmployerName();
    }
    @GetMapping("/find")
    public String findEmployer(@ModelAttribute Customer_EmployerRequest customerEmployerRequest,
                               Model model) {
        if(customerEmployerRequest != null) {
            Optional<Employer> maybeEmployer = employerService.findByEmployerName(customerEmployerRequest.getEmployerName());
            if(maybeEmployer.isPresent()) {
                 model.addAttribute("employer", employerFacade.getEmployerResponseByEmployer(maybeEmployer.get()));
                List<Customer> customers = ceService.findCustomersByEmployerId(maybeEmployer.get().getId());
                List<CustomerResponse> customerResponses = customers.stream()
                        .map(customerFacade::getCustomerResponseByCustomer)
                        .collect(Collectors.toList());

                model.addAttribute("customers", customerResponses);
            }else if(customerEmployerRequest.getEmployerName()!=null) {
                model.addAttribute("error","Не знайдено компанію з назвою: "+ customerEmployerRequest.getEmployerName());
            }

        }
        return "search-employer";
    }
    @GetMapping("/all")
    public String allEmployers(@RequestParam(defaultValue = "1") int page,
                               @RequestParam(defaultValue = "10") int size,
                               Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Employer> employerPage = employerService.findAll(pageable);

        List<EmployerResponse> employers = employerPage.getContent().stream()
                .map(employerFacade::getEmployerResponseByEmployer)
                .collect(Collectors.toList());

        model.addAttribute("employers", employers);
        model.addAttribute("currentPage", employerPage.getNumber() + 1); // Додаємо 1, щоб сторінки були зручні
        model.addAttribute("totalPages", employerPage.getTotalPages());
        model.addAttribute("totalEmployers", employerPage.getTotalElements());
        model.addAttribute("size", size);

        return "employers";
    }


}
