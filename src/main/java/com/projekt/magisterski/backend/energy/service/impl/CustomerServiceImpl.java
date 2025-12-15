package com.projekt.magisterski.backend.energy.service.impl;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.service.CustomerService;
import com.projekt.magisterski.backend.energy.web.dto.CustomerDto;
import com.projekt.magisterski.backend.energy.web.dto.CustomerRegistrationDto;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;


    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public Customer save(CustomerRegistrationDto customerRegistrationDto) {
        Customer customer = new Customer(customerRegistrationDto.getFirstName(), customerRegistrationDto.getLastName(),customerRegistrationDto.getBirthDate(),
                customerRegistrationDto.getPPE(), customerRegistrationDto.getEmail(), customerRegistrationDto.getContractNumber(), customerRegistrationDto.isProsument());
        return customerRepository.save(customer);
    }

    @Override
    public Customer update(Long customerId, CustomerDto customerDto) {
        Optional<Customer> customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Customer existingCustomer = customerOptional.get();
            existingCustomer.setFirstName(customerDto.getFirstName());
            existingCustomer.setLastName(customerDto.getLastName());
            existingCustomer.setBirthDate(customerDto.getBirthDate());
            existingCustomer.setPPE(customerDto.getPPE());
            existingCustomer.setEmail(customerDto.getEmail());
            existingCustomer.setContractNumber(customerDto.getContractNumber());
            existingCustomer.setProsument(customerDto.isProsument());

            return customerRepository.save(existingCustomer);
        } else {
            throw new EntityNotFoundException("Customer not found with ID: " + customerId);
        }
    }
}
