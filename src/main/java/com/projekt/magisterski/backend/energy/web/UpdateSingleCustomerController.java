package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.service.CustomerService;
import com.projekt.magisterski.backend.energy.web.dto.CustomerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/update-single-customer")
public class UpdateSingleCustomerController {

    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    public UpdateSingleCustomerController(CustomerRepository customerRepository, CustomerService customerService) {
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    @GetMapping("/{customerId}")
    public String getupdatesinglecustomer(@PathVariable Long customerId, Model model) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);
        Customer customer = customerOptional.get();
        model.addAttribute("customer", customer);
        return "update-single-customer";
    }

    @PostMapping
    public String updatesinglecustomer(@ModelAttribute("customer") CustomerDto customerDto) {
        customerService.update(customerDto.getId(), customerDto);
        return "update-single-customer";
    }

}
