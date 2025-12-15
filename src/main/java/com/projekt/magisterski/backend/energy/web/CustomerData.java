package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.model.User;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.repo.UserRepository;
import com.projekt.magisterski.backend.energy.web.dto.CustomerDto;
import com.projekt.magisterski.backend.energy.web.dto.UserRegistrationDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;

@Controller
@RequestMapping("/customerdata")
public class CustomerData {

    private CustomerRepository customerRepository;
    private UserRepository userRepository;

    public CustomerData(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String customerdata(Model model)
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email);
        model.addAttribute(customer);
        return "customerdata";
    }

    @PostMapping("/changepersonaldata")
    public String changepersonaldata(@ModelAttribute("customer") CustomerDto customerDto, Model model,
                                                     HttpServletRequest request, HttpServletResponse response){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email);
        customer.setFirstName(customerDto.getFirstName());
        customer.setLastName(customerDto.getLastName());
        customer.setEmail(customerDto.getEmail());
        customerRepository.save(customer);
        User user = userRepository.findByEmail(email);
        user.setFirstName(customerDto.getFirstName());
        user.setLastName(customerDto.getLastName());
        user.setEmail(customerDto.getEmail());
        userRepository.save(user);
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, SecurityContextHolder.getContext().getAuthentication());
        return "login";
    }

    @PostMapping("/changepassword")
    public ResponseEntity<String> changePassword(@RequestParam String oldPassword,
                                         @RequestParam String newPassword,
                                         Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Customer customer = customerRepository.findByEmail(email);
        User user = userRepository.findByEmail(customer.getEmail());
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(oldPassword,user.getPassword());
        model.addAttribute(customer);
        if(matches){
            String noweZakodowaneHaslo = passwordEncoder.encode(newPassword);
            user.setPassword(noweZakodowaneHaslo);
            userRepository.save(user);
            return ResponseEntity.ok("Hasło zostało zmienione");
        }
        else{
            return ResponseEntity.badRequest().body("Hasło zostało zmienione");
        }
    }
}
