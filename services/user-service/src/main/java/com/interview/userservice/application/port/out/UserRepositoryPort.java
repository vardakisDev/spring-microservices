package com.interview.userservice.application.port.out;

import com.interview.userservice.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepositoryPort {

    Optional<User> findById(UUID userId);

    Optional<User> findByEmail(String email);

    List<User> findAll();

    User save(User user);

    void deleteById(UUID userId);
}