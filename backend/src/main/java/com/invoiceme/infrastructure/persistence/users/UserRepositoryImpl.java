package com.invoiceme.infrastructure.persistence.users;

import com.invoiceme.domain.users.User;
import com.invoiceme.domain.users.UserRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA implementation of UserRepository.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;

    public UserRepositoryImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(User user) {
        UserEntity entity = jpaRepository.findById(user.getId())
            .map(existing -> {
                existing.setUsername(user.getUsername());
                existing.setEmail(user.getEmail());
                existing.setPasswordHash(user.getPasswordHash());
                return existing;
            })
            .orElse(new UserEntity(user));
        jpaRepository.save(entity);
    }

    @Override
    public Optional<User> findById(UUID id) {
        return jpaRepository.findById(id)
            .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaRepository.findByUsername(username)
            .map(UserEntity::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
            .map(UserEntity::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }
}

