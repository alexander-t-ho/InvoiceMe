package com.invoiceme.application.auth.login;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.users.User;
import com.invoiceme.domain.users.UserRepository;
import com.invoiceme.infrastructure.security.JwtService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Handler for user login.
 */
@Service
public class LoginHandler {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    public LoginHandler(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Transactional(readOnly = true)
    public LoginResult handle(LoginCommand command) {
        // Find user by username
        Optional<User> userOpt = userRepository.findByUsername(command.username());
        if (userOpt.isEmpty()) {
            throw new DomainValidationException("Invalid username or password");
        }

        User user = userOpt.get();

        // Validate password
        if (!user.validatePassword(command.password())) {
            throw new DomainValidationException("Invalid username or password");
        }

        // Generate JWT token
        String token = jwtService.generateToken(user.getUsername());

        return new LoginResult(token, user.getUsername(), user.getEmail());
    }

    public record LoginResult(String token, String username, String email) {
    }
}

