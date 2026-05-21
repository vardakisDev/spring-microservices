package com.interview.userservice.infrastructure.web;

import com.interview.userservice.api.UsersApi;
import com.interview.userservice.api.model.CreateUserRequest;
import com.interview.userservice.api.model.UpdateUserRequest;
import com.interview.userservice.api.model.UserCollectionResponse;
import com.interview.userservice.api.model.UserResponse;
import com.interview.userservice.application.usecase.CreateUserUseCase;
import com.interview.userservice.application.usecase.DeleteUserUseCase;
import com.interview.userservice.application.usecase.GetUserUseCase;
import com.interview.userservice.application.usecase.ListUsersUseCase;
import com.interview.userservice.application.usecase.UpdateUserUseCase;
import com.interview.userservice.application.usecase.command.CreateUserCommand;
import com.interview.userservice.application.usecase.command.UpdateUserCommand;
import com.interview.userservice.domain.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class UserController implements UsersApi {

    private final CreateUserUseCase createUserUseCase;
    private final GetUserUseCase getUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final UpdateUserUseCase updateUserUseCase;
    private final DeleteUserUseCase deleteUserUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          GetUserUseCase getUserUseCase,
                          ListUsersUseCase listUsersUseCase,
                          UpdateUserUseCase updateUserUseCase,
                          DeleteUserUseCase deleteUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.getUserUseCase = getUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.updateUserUseCase = updateUserUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
    }

    @Override
    public ResponseEntity<UserResponse> createUser(CreateUserRequest createUserRequest) {
        User user = createUserUseCase.execute(new CreateUserCommand(
                createUserRequest.getEmail(),
                createUserRequest.getFirstName(),
                createUserRequest.getLastName(),
                createUserRequest.getVip()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(user));
    }

    @Override
    public ResponseEntity<Void> deleteUser(UUID userId) {
        deleteUserUseCase.execute(userId);
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<UserResponse> getUser(UUID userId) {
        return ResponseEntity.ok(toResponse(getUserUseCase.execute(userId)));
    }

    @Override
    public ResponseEntity<UserCollectionResponse> listUsers() {
        UserCollectionResponse response = new UserCollectionResponse();
        response.setUsers(listUsersUseCase.execute().stream().map(this::toResponse).toList());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<UserResponse> updateUser(UUID userId, UpdateUserRequest updateUserRequest) {
        User user = updateUserUseCase.execute(userId, new UpdateUserCommand(
                updateUserRequest.getEmail(),
                updateUserRequest.getFirstName(),
                updateUserRequest.getLastName(),
                updateUserRequest.getVip()));
        return ResponseEntity.ok(toResponse(user));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse()
                .id(user.id())
                .email(user.email())
                .firstName(user.firstName())
                .lastName(user.lastName())
                .vip(user.vip())
                .createdAt(user.createdAt())
                .updatedAt(user.updatedAt());
    }
}