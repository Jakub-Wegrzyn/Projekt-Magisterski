package com.projekt.magisterski.backend.energy.service;

import com.projekt.magisterski.backend.energy.model.User;
import com.projekt.magisterski.backend.energy.web.dto.UserRegistrationDto;

public interface UserService {
    User save(UserRegistrationDto registrationDto);
}
