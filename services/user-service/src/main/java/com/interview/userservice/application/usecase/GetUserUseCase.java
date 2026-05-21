package com.interview.userservice.application.usecase;

import com.interview.userservice.application.exception.ResourceNotFoundException;
import com.interview.userservice.application.port.out.UserRepositoryPort;
import com.interview.userservice.domain.model.User;

import java.util.Objects;
import java.util.UUID;

public class GetUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public GetUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = Objects.requireNonNull(userRepositoryPort, "userRepositoryPort must not be null");
    }

    public User execute(UUID userId) {
        return userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found: " + userId));
    }
}