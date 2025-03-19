package com.example.SpringWeb.controller;
import com.example.SpringWeb.DTO.*;
import com.example.SpringWeb.config.AppLogger;
import com.example.SpringWeb.facade.AccountFacade;
import com.example.SpringWeb.facade.CustomerFacade;
import com.example.SpringWeb.facade.EmployerFacade;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ModelAttribute;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/customers")
@Validated
public class CustomerController {
    private final CustomerService customerService;
    private final AccountService accountService;
    private final Customer_EmployerService customerEmployerService;
    private final EmployerService employerService;
    private final CustomerFacade customerFacade;
    private final AccountFacade accountFacade;
    private final EmployerFacade employerFacade;
    private final AppLogger appLogger;
    @GetMapping("/error")
    public String errorPage(Model model) {
        return "error";
    }
    @Autowired
    public CustomerController(CustomerService customerService, AccountService accountService,
                              Customer_EmployerService customerEmployerService, EmployerService employerService,
                              CustomerFacade customerFacade,AccountFacade accountFacade,EmployerFacade employerFacade,
                              AppLogger appLogger) {
        this.accountService = accountService;
        this.customerService = customerService;
        this.customerEmployerService = customerEmployerService;
        this.employerService = employerService;
        this.customerFacade = customerFacade;
        this.accountFacade = accountFacade;
        this.employerFacade = employerFacade;
        this.appLogger = appLogger;
    }

    @GetMapping("/all")
    public String showCustomerPage(@RequestParam(defaultValue = "1") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Customer> customerPage = customerService.findAll(pageable);
        List<CustomerResponse> customers = customerPage.getContent().stream()
                .map(customerFacade::getCustomerResponseByCustomer)
                .collect(Collectors.toList());

        model.addAttribute("customers", customers);
        model.addAttribute("currentPage", customerPage.getNumber() + 1); // Додаємо 1 для зручності користувача
        model.addAttribute("totalPages", customerPage.getTotalPages());
        model.addAttribute("totalCustomers", customerPage.getTotalElements());
        appLogger.logInfo("Отримуємо сторінку списку клієнтів");

        return "customers";
    }
    @ModelAttribute("customerRequest")
    public CustomerRequest customerRequest() {
        return new CustomerRequest();
    }


    @PostMapping("/add")
    public String addCustomer(@ModelAttribute @Validated CustomerRequest customerRequest,BindingResult bindingResult,
                              Model model, RedirectAttributes redirectAttributes) {
        String errorMessage;
        try{
            if (bindingResult.hasErrors() || !bindingResult.getAllErrors().isEmpty()) {
               errorMessage = bindingResult.getFieldError().getDefaultMessage();
               redirectAttributes.addAttribute("message", errorMessage);
               appLogger.logWarn(errorMessage);
               return "redirect:/customers/error";
            }else{
                customerService.save(customerFacade.getCustomerByCustomerRequest(customerRequest));
                redirectAttributes.addFlashAttribute("success", "Клієнта додано успішно");
                appLogger.logInfo("Клієнта додано успішно");
            }
        }catch (DataAccessException e){
            model.addAttribute("message","Помилка доступу до бази даних");
            errorMessage = e.getMessage();
            appLogger.logError(errorMessage,e);
        }
        return "redirect:/customers/create";
    }

    @GetMapping("/create")
    public String showCreateForm() {
        return "create";
    }
    @GetMapping("/find")
    public String findCustomerById(@RequestParam(name = "id",required = false) Long id, Model model) {
        String errorMessage;
        try {
            if(id != null){
                Optional<Customer> customer = customerService.findById(id);
                if(customer.isPresent()){

                    model.addAttribute("customer", customerFacade.getCustomerResponseByCustomer(customer.get()));
                    Optional<List<Account>> accounts = accountService.findByCustomerId(customer.get().getId());
                    accounts.ifPresent(accountList -> {
                        List<AccountResponse> accountResponses = accountList.stream()
                                .map(accountFacade::getAccountResponseByAccount)
                                .collect(Collectors.toList());
                        model.addAttribute("accounts", accountResponses);
                    });

                    Optional<List<Employer>> employers = Optional.of(customerEmployerService.findEmployersByCustomerId(id));
                    employers.ifPresent(employerList -> {
                        List<EmployerResponse> employerResponses = employerList.stream()
                                .map(employerFacade::getEmployerResponseByEmployer)
                                .collect(Collectors.toList());
                        model.addAttribute("employers", employerResponses);
                    });
                    appLogger.logInfo("Користувача успішно знайдено");
                }
                else {
                    errorMessage = "Не знайдено відповідного користувача";
                    model.addAttribute("error", errorMessage);
                    appLogger.logWarn(errorMessage);
                }
            }

        }catch (DataAccessException e){
            model.addAttribute("error","Помилка доступу до БД");
            errorMessage = e.getMessage();
            appLogger.logError(errorMessage,e);
        }
        return "search";

    }
    @GetMapping("/change")
    public String changeCustomerById(@ModelAttribute CustomerRequest customerRequest,
                                     RedirectAttributes redirectAttributes,
                                     Model model) {
        String errorMessage;
        try {
            if(customerRequest.getId() != null) {
                Optional<Customer> customer = customerService.findById(customerRequest.getId());
                if (customer.isPresent()) {
                    CustomerResponse customerResponse = customerFacade.getCustomerResponseByCustomer(customer.get());
                    Optional<List<Account>> customerAccounts = accountService.findByCustomerId(customerRequest.getId());
                    customerAccounts.ifPresent(accounts ->
                            customerResponse.setAccounts(
                                    accounts.stream()
                                            .map(accountFacade::getAccountResponseByAccount)
                                            .collect(Collectors.toList())
                            )
                    );
                    model.addAttribute("customer", customerResponse);
                    model.addAttribute("accounts", customerResponse.getAccounts());
                    Optional<List<Employer>> employers = Optional.of(customerEmployerService.findEmployersByCustomerId(customerRequest.getId()));

                    employers.ifPresent(employerList ->
                            model.addAttribute("employers",
                                    employerList.stream()
                                            .map(employerFacade::getEmployerResponseByEmployer)
                                            .collect(Collectors.toList())
                            )
                    );
                    appLogger.logInfo("Потенційне редагування клієнта");
                    return "edit-customer";
                }
            }
        }catch (DataAccessException e){
                redirectAttributes.addAttribute("error", "Помилка доступу до БД");
                errorMessage = e.getMessage();
                appLogger.logError(errorMessage,e);
        }

        return "edit-customer";
    }
    @PostMapping("/update/{id}")
    public String updateCustomer(@PathVariable int id, @ModelAttribute CustomerRequest customer, Model model) {
        boolean updated = customerService.save(customerFacade.getCustomerByCustomerRequest(customer));
        String message;
        if (updated) {
            message = "Дані клієнта успішно оновлено";
            model.addAttribute("success", message);
            appLogger.logInfo("");
        } else {
            message = "Не вдалося оновити клієнта";
            model.addAttribute("error",message );
            appLogger.logWarn(message);
        }
        return "redirect:/customers/change?id=" + id;
    }
    @PostMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable int id, Model model) {
        String message;
        try {
            if(customerService.deleteById(id)) {
                message = "Клієнта з ID " + id + " успішно видалено.";
                model.addAttribute("success",message);
                appLogger.logInfo(message);
            }else {
                message = "Не вдалося знайти клієнта з ID " + id + " для видалення.";
                model.addAttribute("error",message);
                appLogger.logWarn(message);
            }
        }catch (DataAccessException e){
            model.addAttribute("error","Не вдалося видалити клієнта з "+id);
            message = e.getMessage();
            appLogger.logError(message,e);
        }
        return "edit-customer";
    }
    @PostMapping("/remove_from_employer/{customerId}")
    public String removeFromEmployer(@PathVariable Long customerId,
                                     @RequestParam Long employerId,
                                     RedirectAttributes redirectAttributes) {
        String message;
        try {
            Optional<Employer> employer = employerService.findById(employerId);
            if(employer.isPresent()){
                message = "Клієнта з Id "+customerId+" успішно видалено з компанії";
                customerEmployerService.deleteCustomerFromEmployer(customerId, employerId);
                redirectAttributes.addFlashAttribute("success",message);
                appLogger.logInfo(message);
            }
            else {
                message = "Не знайдено компанію з Id " + employerId;
                redirectAttributes.addFlashAttribute("error",message);
                appLogger.logWarn(message);
            }

        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до БД");
            message = e.getMessage();
            appLogger.logError(message,e);
        }
        return "redirect:/customers/change?id=" + customerId;
    }
    @PostMapping("/add_to_employer")
    public String addToEmployer(@ModelAttribute @Validated Customer_EmployerRequest customerRequest,
                                BindingResult bindingResult,
                                RedirectAttributes redirectAttributes) {
        String message;
        try {
            if(bindingResult.hasErrors()) {
                message = bindingResult.getFieldError().getDefaultMessage();
                redirectAttributes.addAttribute("message",message);
                appLogger.logWarn(message);
                return "redirect:/customers/error";
            }
            Optional<Employer> maybeEmployer = employerService.findByEmployerName(customerRequest.getEmployerName());
            if(maybeEmployer.isPresent()){
                Optional<Customer_Employer> maybeCE = customerEmployerService.findEmployerByCustomerIdAndEmployerId(customerRequest.getCustomerId(), maybeEmployer.get().getId());
                if(maybeCE.isPresent()){
                    message = "Відповідний контракт між користувачем і компанією вже підписаний";
                    redirectAttributes.addFlashAttribute("error",message);
                    appLogger.logWarn(message);
                }else {
                    Optional<Customer> customerOpt = customerService.findById(customerRequest.getCustomerId());
                    if (customerOpt.isPresent()) {
                        Customer customer = customerOpt.get();
                        Employer employer = maybeEmployer.get();
                        Customer_Employer customerEmployer = new Customer_Employer(employer,customer);
                        customerEmployerService.save(customerEmployer);
                        appLogger.logInfo("Клієнта успішно додано до компанії");
                    }else{
                        message = "Не знайдено клієнта з Id "+ customerRequest.getCustomerId();
                        redirectAttributes.addFlashAttribute("error",message);
                        appLogger.logWarn(message);
                    }
                }
            }
            else {
                message = "Не знайдено компанію з ім'ям " + customerRequest.getEmployerName();
                redirectAttributes.addFlashAttribute("error", message);
                appLogger.logWarn(message);
            }


        }catch (DataAccessException e){
            redirectAttributes.addFlashAttribute("error","Помилка доступу до БД");
            message = e.getMessage();
            appLogger.logError(message,e);
        }
        return "redirect:/customers/change?id=" + customerRequest.getCustomerId();
    }

}
