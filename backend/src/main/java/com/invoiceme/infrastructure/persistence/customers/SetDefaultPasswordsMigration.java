package com.invoiceme.infrastructure.persistence.customers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;

/**
 * Migration to set default password "123456" for existing customers without passwords.
 * This runs automatically on application startup in development mode.
 * 
 * In production, you should run a proper database migration instead.
 */
@Component
@Profile("!prod") // Only run in non-production environments
public class SetDefaultPasswordsMigration implements CommandLineRunner {
    
    @PersistenceContext
    private EntityManager entityManager;
    
    private final PasswordEncoder passwordEncoder;
    
    public SetDefaultPasswordsMigration(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    @Transactional
    public void run(String... args) {
        // Find all customers without passwords
        List<CustomerEntity> customersWithoutPassword = entityManager
            .createQuery("SELECT c FROM CustomerEntity c WHERE c.passwordHash IS NULL OR c.passwordHash = ''", CustomerEntity.class)
            .getResultList();
        
        if (!customersWithoutPassword.isEmpty()) {
            String defaultPasswordHash = passwordEncoder.encode("123456");
            
            for (CustomerEntity customer : customersWithoutPassword) {
                customer.setPasswordHash(defaultPasswordHash);
                entityManager.merge(customer);
            }
            
            System.out.println("Migration: Set default password for " + customersWithoutPassword.size() + " customers");
        }
    }
}

