package com.invoiceme.integration;

import com.invoiceme.application.auth.login.LoginCommand;
import com.invoiceme.application.auth.login.LoginHandler;
import com.invoiceme.application.auth.register.RegisterUserCommand;
import com.invoiceme.application.auth.register.RegisterUserHandler;
import com.invoiceme.domain.users.UserRepository;
import com.invoiceme.infrastructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test for authentication flow.
 * Tests: Register → Login → Token validation
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Authentication E2E Tests")
class AuthenticationE2ETest {
    
    @Autowired
    private RegisterUserHandler registerUserHandler;
    
    @Autowired
    private LoginHandler loginHandler;
    
    @Autowired
    private JwtService jwtService;
    
    @Autowired
    private UserRepository userRepository;
    
    private String username;
    private String email;
    private String password;
    
    @BeforeEach
    void setUp() {
        username = "testuser_" + UUID.randomUUID().toString().substring(0, 8);
        email = username + "@example.com";
        password = "password123";
    }
    
    @Test
    @DisplayName("Should complete authentication flow: Register → Login → Get Token")
    void shouldCompleteAuthenticationFlow() {
        // Step 1: Register user
        RegisterUserCommand registerCommand = new RegisterUserCommand(
            username,
            email,
            password
        );
        var user = registerUserHandler.handle(registerCommand);
        
        assertNotNull(user);
        assertNotNull(user.getId());
        assertTrue(userRepository.findById(user.getId()).isPresent());
        
        // Step 2: Login with valid credentials
        LoginCommand loginCommand = new LoginCommand(username, password);
        var loginResult = loginHandler.handle(loginCommand);
        
        assertNotNull(loginResult);
        assertNotNull(loginResult.token());
        assertEquals(username, loginResult.username());
        assertEquals(email, loginResult.email());
        
        // Step 3: Validate JWT token
        assertTrue(jwtService.validateToken(loginResult.token(), username));
        String extractedUsername = jwtService.extractUsername(loginResult.token());
        assertEquals(username, extractedUsername);
    }
    
    @Test
    @DisplayName("Should not login with invalid credentials")
    void shouldNotLoginWithInvalidCredentials() {
        // Register user
        RegisterUserCommand registerCommand = new RegisterUserCommand(
            username,
            email,
            password
        );
        registerUserHandler.handle(registerCommand);
        
        // Try to login with wrong password
        LoginCommand loginCommand = new LoginCommand(username, "wrongpassword");
        
        assertThrows(Exception.class, () -> {
            loginHandler.handle(loginCommand);
        });
    }
    
    @Test
    @DisplayName("Should not register duplicate username")
    void shouldNotRegisterDuplicateUsername() {
        // Register user
        RegisterUserCommand registerCommand = new RegisterUserCommand(
            username,
            email,
            password
        );
        registerUserHandler.handle(registerCommand);
        
        // Try to register again with same username
        RegisterUserCommand duplicateCommand = new RegisterUserCommand(
            username,
            "different@example.com",
            "differentpassword"
        );
        
        assertThrows(Exception.class, () -> {
            registerUserHandler.handle(duplicateCommand);
        });
    }
    
    @Test
    @DisplayName("Should not register duplicate email")
    void shouldNotRegisterDuplicateEmail() {
        // Register user
        RegisterUserCommand registerCommand = new RegisterUserCommand(
            username,
            email,
            password
        );
        registerUserHandler.handle(registerCommand);
        
        // Try to register again with same email
        RegisterUserCommand duplicateCommand = new RegisterUserCommand(
            "differentusername",
            email,
            "differentpassword"
        );
        
        assertThrows(Exception.class, () -> {
            registerUserHandler.handle(duplicateCommand);
        });
    }
    
    @Test
    @DisplayName("Should generate valid JWT token with correct claims")
    void shouldGenerateValidJwtTokenWithCorrectClaims() {
        // Register and login
        RegisterUserCommand registerCommand = new RegisterUserCommand(
            username,
            email,
            password
        );
        registerUserHandler.handle(registerCommand);
        
        LoginCommand loginCommand = new LoginCommand(username, password);
        var loginResult = loginHandler.handle(loginCommand);
        
        // Validate token
        String token = loginResult.token();
        assertNotNull(token);
        assertTrue(jwtService.validateToken(token, username));
        
        // Extract and verify claims
        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(username, extractedUsername);
    }
}

