package com.invoiceme.api.auth;

import com.invoiceme.application.auth.login.LoginCommand;
import com.invoiceme.application.auth.login.LoginHandler;
import com.invoiceme.application.auth.register.RegisterUserCommand;
import com.invoiceme.application.auth.register.RegisterUserHandler;
import com.invoiceme.application.auth.unified.UnifiedLoginCommand;
import com.invoiceme.application.auth.unified.UnifiedLoginHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for authentication operations.
 */
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Authentication and authorization API")
public class AuthController {
    private final RegisterUserHandler registerUserHandler;
    private final LoginHandler loginHandler;
    private final UnifiedLoginHandler unifiedLoginHandler;

    public AuthController(
            RegisterUserHandler registerUserHandler, 
            LoginHandler loginHandler,
            UnifiedLoginHandler unifiedLoginHandler) {
        this.registerUserHandler = registerUserHandler;
        this.loginHandler = loginHandler;
        this.unifiedLoginHandler = unifiedLoginHandler;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account")
    @ApiResponse(responseCode = "201", description = "User registered successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input or user already exists")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        RegisterUserCommand command = new RegisterUserCommand(
            request.username(),
            request.email(),
            request.password()
        );

        var user = registerUserHandler.handle(command);

        AuthResponse response = new AuthResponse(
            null, // Token not generated on registration
            user.getUsername(),
            user.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Authenticates a user and returns a JWT token")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "401", description = "Invalid credentials")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = new LoginCommand(
            request.username(),
            request.password()
        );

        var result = loginHandler.handle(command);

        AuthResponse response = new AuthResponse(
            result.token(),
            result.username(),
            result.email()
        );

        return ResponseEntity.ok(response);
    }

    @PostMapping("/unified-login")
    @Operation(summary = "Unified login", description = "Login for both admins and customers. Returns user type and appropriate token.")
    @ApiResponse(responseCode = "200", description = "Login successful")
    @ApiResponse(responseCode = "400", description = "Invalid credentials")
    public ResponseEntity<UnifiedAuthResponse> unifiedLogin(@Valid @RequestBody UnifiedLoginRequest request) {
        UnifiedLoginCommand command = new UnifiedLoginCommand(
            request.identifier(),
            request.password()
        );
        
        var result = unifiedLoginHandler.handle(command);
        
        UnifiedAuthResponse response = new UnifiedAuthResponse(
            result.userType(),
            result.token(),
            result.userId(),
            result.name(),
            result.email(),
            result.customerId()
        );
        
        return ResponseEntity.ok(response);
    }
}

