package com.Rentopia.Rentopia.Controller;

import com.Rentopia.Rentopia.Entity.User;
import com.Rentopia.Rentopia.ServiceLayer.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    private final UserService userService;

    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping("/login")
    public String showLoginPage() {
        return "login"; // Renders login.html
    }
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register"; // Renders register.html
    }
    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user) {
        userService.registerNewUser(user);
        // Redirect to the login page with a success message (optional)
        return "redirect:/login?success";
    }
}