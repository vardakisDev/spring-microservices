package com.interview.userservice.application.usecase;

import com.interview.userservice.application.exception.ConflictException;
import com.interview.userservice.application.port.out.UserRepositoryPort;
import com.interview.userservice.application.usecase.command.CreateUserCommand;
import com.interview.userservice.domain.model.User;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

public class CreateUserUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final Clock clock;

    public CreateUserUseCase(UserRepositoryPort userRepositoryPort, Clock clock) {
        this.userRepositoryPort = Objects.requireNonNull(userRepositoryPort, "userRepositoryPort must not be null");
        this.clock = Objects.requireNonNull(clock, "clock must not be null");
    }

    public User execute(CreateUserCommand command) {
        Objects.requireNonNull(command, "command must not be null");
        userRepositoryPort.findByEmail(command.email()).ifPresent(existing -> {
            throw new ConflictException("user email already exists: " + command.email());
        });

        OffsetDateTime now = OffsetDateTime.now(clock);
        User user = new User(UUID.randomUUID(), command.email(), command.firstName(), command.lastName(), command.vip(), now, now);
        return userRepositoryPort.save(user);
    }
}