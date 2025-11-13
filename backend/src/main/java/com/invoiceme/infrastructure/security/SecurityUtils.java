package com.invoiceme.infrastructure.security;

import com.invoiceme.domain.exceptions.DomainValidationException;
import com.invoiceme.domain.users.User;
import com.invoiceme.domain.users.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Utility class for security-related operations.
 */
@Component
public class SecurityUtils {
    
    private final UserRepository userRepository;
    
    public SecurityUtils(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    /**
     * Gets the current authenticated user ID.
     * @return The user ID
     * @throws DomainValidationException if user is not authenticated or not found
     */
    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new DomainValidationException("User is not authenticated");
        }
        
        String username = authentication.getName();
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new DomainValidationException("User not found: " + username);
        }
        
        return user.get().getId();
    }
    
    /**
     * Gets the current authenticated username.
     * @return The username
     * @throws DomainValidationException if user is not authenticated
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new DomainValidationException("User is not authenticated");
        }
        return authentication.getName();
    }
}









