package com.projekt.magisterski.backend.energy.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "customer_data", uniqueConstraints = {@UniqueConstraint(columnNames = {"email", "PPE", "contract_number"})})
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "birthday")
    private LocalDate birthDate;
    @Column(name = "PPE")
    private String PPE;
    @Column(name = "email")
    private String email;
    @Column(name = "contract_number")
    private String contractNumber;
    @Column(name = "prosument")
    private boolean prosument;

    public Customer(String firstName, String lastName, String PPE, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.PPE = PPE;
        this.email = email;
    }

    public Customer(String firstName, String lastName, LocalDate birthDate, String PPE, String email, String contractNumber, boolean prosument) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.PPE = PPE;
        this.email = email;
        this.contractNumber = contractNumber;
        this.prosument = prosument;
    }

}
