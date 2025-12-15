package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.model.CustomerEnergyUsage;
import com.projekt.magisterski.backend.energy.service.CustomerService;
import com.projekt.magisterski.backend.energy.web.dto.CustomerRegistrationDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/newcustomer")
public class NewCustomerRegistrationController {

    private CustomerService customerService;
    private CustomerEnergyUsage customerEnergyUsage;

    public NewCustomerRegistrationController(CustomerService customerService, CustomerEnergyUsage customerEnergyUsage) {
        super();
        this.customerService = customerService;
        this.customerEnergyUsage = customerEnergyUsage;
    }

    @GetMapping
    public String newcustomer(Model model) {
        model.addAttribute("customer", new Customer());
        return "newcustomer";
    }

    @PostMapping
    public String registerUserAccount(@ModelAttribute("customer") CustomerRegistrationDto customerRegistrationDto) {
        Customer customer = customerService.save(customerRegistrationDto);
        if(customer != null){
            customerEnergyUsage.handleCustomerAddedEvent(customer);
            return "redirect:/newcustomer?success";
        }
        else{
            return "redirect:/newcustomer?failure";
        }

    }
}
