package com.projekt.magisterski.backend.energy.model;

import com.projekt.magisterski.backend.energy.repo.RoleRepository;
import com.projekt.magisterski.backend.energy.repo.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public DataInitializer(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN");
        if (adminRole == null) {
            adminRole = new Role("ROLE_ADMIN");
            roleRepository.save(adminRole);
        }
        Role userRole = roleRepository.findByName("ROLE_USER");
        if (userRole == null) {
            userRole = new Role("ROLE_USER");
            roleRepository.save(userRole);
        }

        for (int i = 1; i <= 5; i++) {
            String userEmail = "admin" + i + "@energy.com";
            if (userRepository.findByEmail(userEmail) == null) {
                User user = new User();
                user.setFirstName("Admin");
                user.setLastName("User" + i);
                user.setEmail(userEmail);
                String encodedPassword = passwordEncoder.encode("password");
                user.setPassword(encodedPassword);
                user.getUser_roles().add(adminRole);
                if (!hasUserRoleRelation(user.getId(), adminRole.getId())) {
                    userRepository.save(user);
                }
            }
        }
    }

    private boolean hasUserRoleRelation(Long userId, Long roleId) {
        String query = "SELECT COUNT(*) FROM users_roles WHERE USER_ID = :userId AND ROLE_ID = :roleId";
        Number result = (Number) entityManager.createNativeQuery(query)
                .setParameter("userId", userId)
                .setParameter("roleId", roleId)
                .getSingleResult();
        return result.intValue() > 0;
    }


}