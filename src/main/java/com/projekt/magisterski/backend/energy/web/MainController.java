package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    @Autowired
    private CustomerRepository customerRepository;

    public MainController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }


    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                if (auth.getAuthority().equals("ROLE_ADMIN")) {
                    return "redirect:/admin";
                } else if (auth.getAuthority().equals("ROLE_USER")) {
                    return "redirect:/customer";
                }
            }
            return "redirect:/dashboard";
        } else {
            return "index";
        }
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @GetMapping("/regulamin")
    public String regulamin() {
        return "regulamin";
    }

    @GetMapping("/customer")
    public String customer() {
        return "customer";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}

