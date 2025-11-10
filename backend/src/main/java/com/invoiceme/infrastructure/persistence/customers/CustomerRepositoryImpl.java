package com.invoiceme.infrastructure.persistence.customers;

import com.invoiceme.domain.customers.Customer;
import com.invoiceme.domain.customers.CustomerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of CustomerRepository using Spring Data JPA.
 * Bridges domain repository interface with JPA implementation.
 */
@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    
    private final CustomerJpaRepository jpaRepository;
    
    public CustomerRepositoryImpl(CustomerJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }
    
    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = CustomerEntity.fromDomain(customer);
        CustomerEntity saved = jpaRepository.save(entity);
        return saved.toDomain();
    }
    
    @Override
    public Optional<Customer> findById(UUID id) {
        return jpaRepository.findById(id)
                .map(CustomerEntity::toDomain);
    }
    
    @Override
    public Optional<Customer> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(CustomerEntity::toDomain);
    }
    
    @Override
    public List<Customer> findAll(int page, int size, String sortBy) {
        Sort sort = Sort.by(Sort.Direction.ASC, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<CustomerEntity> customerPage = jpaRepository.findAll(pageable);
        return customerPage.getContent().stream()
                .map(CustomerEntity::toDomain)
                .collect(Collectors.toList());
    }
    
    @Override
    public long count() {
        return jpaRepository.count();
    }
    
    @Override
    public boolean existsById(UUID id) {
        return jpaRepository.existsById(id);
    }
    
    @Override
    public void deleteById(UUID id) {
        jpaRepository.deleteById(id);
    }
    
    @Override
    public boolean hasInvoices(UUID customerId) {
        return jpaRepository.hasInvoices(customerId);
    }
}


