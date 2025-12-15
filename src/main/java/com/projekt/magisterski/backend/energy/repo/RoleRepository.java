package com.projekt.magisterski.backend.energy.repo;

import com.projekt.magisterski.backend.energy.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
