package com.interview.cartservice.infrastructure.client;

import com.interview.cartservice.application.exception.ResourceNotFoundException;
import com.interview.cartservice.application.port.out.UserDirectoryPort;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

public class UserServiceHttpClientAdapter implements UserDirectoryPort {

    private final RestClient restClient;

    public UserServiceHttpClientAdapter(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public UserSnapshot getUser(UUID userId) {
        try {
            UserResponse response = restClient.get()
                    .uri("/users/{userId}", userId)
                    .retrieve()
                    .body(UserResponse.class);

            if (response == null || response.id() == null) {
                throw new ResourceNotFoundException("user not found: " + userId);
            }

            return new UserSnapshot(response.id(), Boolean.TRUE.equals(response.vip()));
        } catch (RestClientResponseException exception) {
            if (HttpStatusCode.valueOf(exception.getStatusCode().value()).value() == 404) {
                throw new ResourceNotFoundException("user not found: " + userId);
            }
            throw exception;
        }
    }

    private record UserResponse(UUID id, Boolean vip) {
    }
}