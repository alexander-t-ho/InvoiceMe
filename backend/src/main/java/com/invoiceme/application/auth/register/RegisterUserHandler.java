package com.invoiceme.application.auth.register;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.users.User;
import com.invoiceme.domain.users.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Handler for registering a new user.
 */
@Service
public class RegisterUserHandler {
    private final UserRepository userRepository;

    public RegisterUserHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User handle(RegisterUserCommand command) {
        // Check if username already exists
        if (userRepository.existsByUsername(command.username())) {
            throw new DomainValidationException("Username already exists");
        }

        // Check if email already exists
        if (userRepository.existsByEmail(command.email())) {
            throw new DomainValidationException("Email already exists");
        }

        // Create and save user
        User user = User.create(
            command.username(),
            command.email(),
            command.password()
        );

        userRepository.save(user);
        return user;
    }
}

