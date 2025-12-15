package com.projekt.magisterski.backend.energy.service.impl;

import com.projekt.magisterski.backend.energy.model.Customer;
import com.projekt.magisterski.backend.energy.model.Role;
import com.projekt.magisterski.backend.energy.model.User;
import com.projekt.magisterski.backend.energy.repo.CustomerRepository;
import com.projekt.magisterski.backend.energy.repo.RoleRepository;
import com.projekt.magisterski.backend.energy.repo.UserRepository;
import com.projekt.magisterski.backend.energy.service.UserService;
import com.projekt.magisterski.backend.energy.web.dto.UserRegistrationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class UserServiceImpl implements UserService {

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private CustomerRepository customerRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, CustomerRepository customerRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public User save(UserRegistrationDto registrationDto) {
        Customer customer = customerRepository.findByContractNumber(registrationDto.getContractNumber());
        if (customer != null) {
            String firstName = customer.getFirstName();
            String lastName = customer.getLastName();
            String email = customer.getEmail();
            String ppe = customer.getPPE();
            String contractNumber = customer.getContractNumber();
            if (firstName.equals(registrationDto.getFirstName()) &&
                    lastName.equals(registrationDto.getLastName()) &&
                    email.equals(registrationDto.getEmail()) &&
                    ppe.equals(registrationDto.getPPE()) &&
                    contractNumber.equals(registrationDto.getContractNumber())) {
                Role roleUser = roleRepository.findByName("ROLE_USER");
                User user = new User(registrationDto.getFirstName(), registrationDto.getLastName(), registrationDto.getEmail(),
                        passwordEncoder.encode(registrationDto.getPassword()), Arrays.asList(roleUser));
                return userRepository.save(user);
            } else {
                return null;
            }
        }
        else{
            return null;
        }
    }
}
