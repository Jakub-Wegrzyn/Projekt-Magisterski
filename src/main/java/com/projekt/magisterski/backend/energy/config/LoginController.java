package com.projekt.magisterski.backend.energy.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/dashboard")
    public String dashboard(Authentication authentication) {
        if (authentication != null) {
            for (GrantedAuthority auth : authentication.getAuthorities()) {
                if (auth.getAuthority().equals("ROLE_ADMIN")) {
                    return "redirect:/admin";
                } else if (auth.getAuthority().equals("ROLE_USER")) {
                    return "redirect:/customer";
                }
            }
        }
        return "redirect:/dashboard";
    }
}
