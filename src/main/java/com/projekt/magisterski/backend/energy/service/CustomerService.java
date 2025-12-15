package com.projekt.magisterski.backend.energy.service;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.web.dto.CustomerDto;
import com.projekt.magisterski.backend.energy.web.dto.CustomerRegistrationDto;

public interface CustomerService {
    Customer save(CustomerRegistrationDto customerRegistrationDto);
    Customer update(Long customerId, CustomerDto customerDto);
}
