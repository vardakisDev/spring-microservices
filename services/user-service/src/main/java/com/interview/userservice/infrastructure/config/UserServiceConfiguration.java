package com.interview.userservice.infrastructure.config;

import com.interview.userservice.application.port.out.UserRepositoryPort;
import com.interview.userservice.application.usecase.CreateUserUseCase;
import com.interview.userservice.application.usecase.DeleteUserUseCase;
import com.interview.userservice.application.usecase.GetUserUseCase;
import com.interview.userservice.application.usecase.ListUsersUseCase;
import com.interview.userservice.application.usecase.UpdateUserUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class UserServiceConfiguration {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    CreateUserUseCase createUserUseCase(UserRepositoryPort userRepositoryPort, Clock clock) {
        return new CreateUserUseCase(userRepositoryPort, clock);
    }

    @Bean
    GetUserUseCase getUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new GetUserUseCase(userRepositoryPort);
    }

    @Bean
    ListUsersUseCase listUsersUseCase(UserRepositoryPort userRepositoryPort) {
        return new ListUsersUseCase(userRepositoryPort);
    }

    @Bean
    UpdateUserUseCase updateUserUseCase(UserRepositoryPort userRepositoryPort, Clock clock) {
        return new UpdateUserUseCase(userRepositoryPort, clock);
    }

    @Bean
    DeleteUserUseCase deleteUserUseCase(UserRepositoryPort userRepositoryPort) {
        return new DeleteUserUseCase(userRepositoryPort);
    }
}