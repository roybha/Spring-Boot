package com.example.SpringWeb.controller;
import com.example.SpringWeb.DTO.AdminRequest;
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

    @Autowired
    private AdminService adminService;
    @Autowired
    private AdminFacade adminFacade;

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("admin", new AdminRequest());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute @Validated AdminRequest admin,BindingResult bindingResult ,RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/register";
        }
        if(adminService.checkIfAdminExists(admin.getUsername())){
            redirectAttributes.addFlashAttribute("error","Вже існує адміністратор з таким іменем");
            return "redirect:/register";
        }
        adminService.saveAdmin(adminFacade.getAdminByAdminRequest(admin));
        redirectAttributes.addFlashAttribute("message", "Реєстрація успішна! Тепер ви можете увійти.");
        return "redirect:/login";
    }
}
