package com.projekt.magisterski.backend.energy.web;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/listofcustomers")
public class ListOfCustomersController {

    @Autowired
    private CustomerRepository customerRepository;

    public ListOfCustomersController(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @GetMapping
    public String listofcustomers(Model model) {
        List<Customer> customers = customerRepository.findAll();
        model.addAttribute("customers", customers);
        return "listofcustomers";
    }

    @PostMapping
    public ResponseEntity<String> universalMethod(@RequestParam Long customerId, String action, Model model, RedirectAttributes redirectAttributes ) {
        if(action.equals("update")){
            Optional<Customer> customer = customerRepository.findById(customerId);
            redirectAttributes.addAttribute("customerId", customer.get().getId());

            String redirectUrl = UriComponentsBuilder.fromPath("/update-single-customer/{customerId}")
                    .buildAndExpand(customerId)
                    .toUriString();
            return ResponseEntity.ok().body(redirectUrl);
        }
        else if(action.equals("usun")){
            customerRepository.deleteById(customerId);
            List<Customer> customers = customerRepository.findAll();
            model.addAttribute("customers", customers);
            String redirectUrl = UriComponentsBuilder.fromPath("/update-single-customer}")
                    .buildAndExpand(customers)
                    .toUriString();
            return ResponseEntity.ok().body(redirectUrl);
        }
        return null;
    }
}
