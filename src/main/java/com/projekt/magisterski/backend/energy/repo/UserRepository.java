package com.projekt.magisterski.backend.energy.repo;

import com.projekt.magisterski.backend.energy.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String adminEmail);
}
