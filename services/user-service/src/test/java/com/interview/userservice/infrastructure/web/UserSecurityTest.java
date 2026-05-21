package com.interview.userservice.infrastructure.web;

import com.interview.userservice.application.port.out.UserRepositoryPort;
import com.interview.userservice.application.usecase.CreateUserUseCase;
import com.interview.userservice.application.usecase.DeleteUserUseCase;
import com.interview.userservice.application.usecase.GetUserUseCase;
import com.interview.userservice.application.usecase.ListUsersUseCase;
import com.interview.userservice.application.usecase.UpdateUserUseCase;
import com.interview.userservice.domain.model.User;
import com.interview.userservice.infrastructure.config.LocalJwtSecurityConfiguration;
import com.interview.userservice.infrastructure.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Clock;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import({SecurityConfig.class, LocalJwtSecurityConfiguration.class, UserSecurityTest.StubUseCaseConfiguration.class})
class UserSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtEncoder jwtEncoder;

    @Test
    void rejectsAnonymousRequest() throws Exception {
        mockMvc.perform(delete("/users/{userId}", UUID.randomUUID()))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"));
    }

    @Test
    void acceptsValidBearerToken() throws Exception {
        mockMvc.perform(delete("/users/{userId}", UUID.randomUUID())
                        .header(AUTHORIZATION, "Bearer " + tokenForAudience("user-service")))
                .andExpect(status().isNoContent());
    }

    private String tokenForAudience(String audience) {
        Instant issuedAt = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("shopping-platform-local")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(300))
                .subject("test-client")
                .audience(List.of(audience))
                .claim("scope", "test")
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims)).getTokenValue();
    }

    @TestConfiguration
    static class StubUseCaseConfiguration {

        @Bean
        CreateUserUseCase createUserUseCase(UserRepositoryPort userRepositoryPort) {
            return new CreateUserUseCase(userRepositoryPort, Clock.systemUTC());
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
        UpdateUserUseCase updateUserUseCase(UserRepositoryPort userRepositoryPort) {
            return new UpdateUserUseCase(userRepositoryPort, Clock.systemUTC());
        }

        @Bean
        DeleteUserUseCase deleteUserUseCase(UserRepositoryPort userRepositoryPort) {
            return new DeleteUserUseCase(userRepositoryPort);
        }

        @Bean
        UserRepositoryPort userRepositoryPort() {
            return new UserRepositoryPort() {
                @Override
                public Optional<User> findById(UUID userId) {
                    OffsetDateTime now = OffsetDateTime.now();
                    return Optional.of(new User(userId, "john@example.com", "John", "Doe", true, now, now));
                }

                @Override
                public Optional<User> findByEmail(String email) {
                    return Optional.empty();
                }

                @Override
                public List<User> findAll() {
                    OffsetDateTime now = OffsetDateTime.now();
                    return List.of(new User(UUID.randomUUID(), "john@example.com", "John", "Doe", true, now, now));
                }

                @Override
                public User save(User user) {
                    return user;
                }

                @Override
                public void deleteById(UUID userId) {
                }
            };
        }
    }
}