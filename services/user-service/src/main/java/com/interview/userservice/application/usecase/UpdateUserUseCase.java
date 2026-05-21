package com.interview.userservice.application.usecase;

import com.interview.userservice.application.exception.ConflictException;
import com.interview.userservice.application.exception.ResourceNotFoundException;
import com.interview.userservice.application.port.out.UserRepositoryPort;
import com.interview.userservice.application.usecase.command.UpdateUserCommand;
import com.interview.userservice.domain.model.User;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class UpdateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final Clock clock;

    public UpdateUserUseCase(UserRepositoryPort userRepositoryPort, Clock clock) {
        this.userRepositoryPort = Objects.requireNonNull(userRepositoryPort, "userRepositoryPort must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public User execute(UUID userId, UpdateUserCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        User user = userRepositoryPort.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("user not found: " + userId));

        userRepositoryPort.findByEmail(command.email())
                .filter(existing -> !existing.id().equals(userId))
                .ifPresent(existing -> {
                    throw new ConflictException("user email already exists: " + command.email());
                });

        user.update(command.email(), command.firstName(), command.lastName(), command.vip(), OffsetDateTime.now(clock));
        return userRepositoryPort.save(user);
    }
}