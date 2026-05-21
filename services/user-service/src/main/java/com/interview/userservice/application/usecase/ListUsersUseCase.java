package com.interview.userservice.application.usecase;

import com.interview.userservice.application.port.out.UserRepositoryPort;
import com.interview.userservice.domain.model.User;

import java.util.List;
import java.util.Objects;

public class ListUsersUseCase {

    private final UserRepositoryPort userRepositoryPort;

    public ListUsersUseCase(UserRepositoryPort userRepositoryPort) {
        this.userRepositoryPort = Objects.requireNonNull(userRepositoryPort, "userRepositoryPort must not be null");
    }

    public List<User> execute() {
        return userRepositoryPort.findAll();
    }
}