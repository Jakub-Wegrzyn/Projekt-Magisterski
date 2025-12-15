package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.model.User;
import com.projekt.magisterski.backend.energy.service.UserService;
import com.projekt.magisterski.backend.energy.web.dto.UserRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/registration")
public class UserRegistrationController {

    private UserService userService;

    public UserRegistrationController(UserService userService) {
        super();
        this.userService = userService;
    }

    @ModelAttribute("user")
    public UserRegistrationDto userRegistrationDto() {
        return new UserRegistrationDto();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "registration";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("user") UserRegistrationDto registrationDto) {
        User user = userService.save(registrationDto);
        if (user != null) {
            return "redirect:/registration?success";
        } else {
            return "redirect:/registration?failure";
        }
    }
}

