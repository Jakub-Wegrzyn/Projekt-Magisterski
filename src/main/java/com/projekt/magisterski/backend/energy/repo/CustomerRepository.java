package com.projekt.magisterski.backend.energy.repo;

import com.projekt.magisterski.backend.energy.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findById(Long id);
    List<Customer> findAll();
    Customer findByContractNumber(String contractNumber);
    Customer findByEmail(String email);
}
