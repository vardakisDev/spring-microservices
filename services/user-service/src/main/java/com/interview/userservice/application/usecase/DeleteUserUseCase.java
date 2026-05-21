package com.interview.userservice.application.usecase;

import com.interview.userservice.application.exception.ResourceNotFoundException;
import com.interview.userservice.application.port.out.UserRepositoryPort;

import java.util.Objects;
import java.util.UUID;

public class DeleteUserUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public DeleteUserUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = Objects.requireNonNull(userRepositoryPort, "userRepositoryPort must not be null");
    }

    public void execute(UUID userId) {
        if (userRepositoryPort.findById(userId).isEmpty()) {
            throw new ResourceNotFoundException("user not found: " + userId);
        }
        userRepositoryPort.deleteById(userId);
    }
}