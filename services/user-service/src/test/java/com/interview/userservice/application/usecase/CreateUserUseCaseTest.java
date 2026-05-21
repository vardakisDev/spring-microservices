package com.interview.userservice.application.usecase;

import com.interview.userservice.application.exception.ConflictException;
import com.interview.userservice.application.port.out.UserRepositoryPort;
import com.interview.userservice.application.usecase.command.CreateUserCommand;
import com.interview.userservice.domain.model.User;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateUserUseCaseTest {

    private final InMemoryUserRepository userRepository = new InMemoryUserRepository();
    private final Clock clock = Clock.fixed(Instant.parse("2026-05-19T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void createsUserWhenEmailIsUnique() {
        CreateUserUseCase useCase = new CreateUserUseCase(userRepository, clock);

        User user = useCase.execute(new CreateUserCommand("john@example.com", "John", "Doe", true));

        assertThat(user.email()).isEqualTo("john@example.com");
        assertThat(user.vip()).isTrue();
        assertThat(userRepository.findById(user.id())).contains(user);
    }

    @Test
    void rejectsDuplicateEmails() {
        CreateUserUseCase useCase = new CreateUserUseCase(userRepository, clock);
        useCase.execute(new CreateUserCommand("john@example.com", "John", "Doe", false));

        assertThatThrownBy(() -> useCase.execute(new CreateUserCommand("john@example.com", "Jane", "Doe", true)))
                .isInstanceOf(ConflictException.class)
                .hasMessage("user email already exists: john@example.com");
    }

    private static final class InMemoryUserRepository implements UserRepositoryPort {
        private final Map<UUID, User> users = new HashMap<>();

        @Override
        public Optional<User> findById(UUID userId) { return Optional.ofNullable(users.get(userId)); }
        @Override
        public Optional<User> findByEmail(String email) { return users.values().stream().filter(u -> u.email().equalsIgnoreCase(email)).findFirst(); }
        @Override
        public List<User> findAll() { return users.values().stream().toList(); }
        @Override
        public User save(User user) { users.put(user.id(), user); return user; }
        @Override
        public void deleteById(UUID userId) { users.remove(userId); }
    }
}