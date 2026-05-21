package com.interview.userservice.infrastructure.persistence;

import com.interview.userservice.application.port.out.UserRepositoryPort;
import com.interview.userservice.domain.model.User;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaUserRepositoryAdapter implements UserRepositoryPort {

    private final UserJpaRepository userJpaRepository;

    public JpaUserRepositoryAdapter(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return userJpaRepository.findById(userId).map(this::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmailIgnoreCase(email).map(this::toDomain);
    }

    @Override
    public List<User> findAll() {
        return userJpaRepository.findAll().stream()
                .sorted(java.util.Comparator.comparing(UserEntity::getCreatedAt))
                .map(this::toDomain)
                .toList();
    }

    @Override
    public User save(User user) {
        return toDomain(userJpaRepository.save(toEntity(user)));
    }

    @Override
    public void deleteById(UUID userId) {
        userJpaRepository.deleteById(userId);
    }

    private UserEntity toEntity(User user) {
        UserEntity entity = new UserEntity();
        entity.setId(user.id());
        entity.setEmail(user.email());
        entity.setFirstName(user.firstName());
        entity.setLastName(user.lastName());
        entity.setVip(user.vip());
        entity.setCreatedAt(user.createdAt());
        entity.setUpdatedAt(user.updatedAt());
        return entity;
    }

    private User toDomain(UserEntity entity) {
        return new User(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.isVip(),
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }
}