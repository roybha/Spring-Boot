package com.example.SpringWeb.controller;
import com.example.SpringWeb.DTO.AdminRequest;
import com.example.SpringWeb.config.AppLogger;
import com.example.SpringWeb.facade.AdminFacade;
import com.example.SpringWeb.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {

    private final AdminService adminService;

    private final AdminFacade adminFacade;

    private final AppLogger appLogger;
    @Autowired
    public RegistrationController(AppLogger appLogger, AdminService adminService, AdminFacade adminFacade) {
        this.appLogger = appLogger;
        this.adminService = adminService;
        this.adminFacade = adminFacade;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("admin", new AdminRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Validated AdminRequest admin,BindingResult bindingResult ,RedirectAttributes redirectAttributes) {
        String message;
        if (bindingResult.hasErrors()) {
            message =  bindingResult.getFieldError().getDefaultMessage();
            redirectAttributes.addFlashAttribute("error",message);
            appLogger.logWarn(message);
            return "redirect:/register";
        }
        if(adminService.checkIfAdminExists(admin.getUsername())){
            message = "Вже існує адміністратор з таким іменем";
            redirectAttributes.addFlashAttribute("error",message);
            appLogger.logWarn(message);
            return "redirect:/register";
        }
        adminService.saveAdmin(adminFacade.getAdminByAdminRequest(admin));
        message = "Реєстрація успішна";
        redirectAttributes.addFlashAttribute("message",message);
        appLogger.logInfo(message);
        return "redirect:/login";
    }
}
