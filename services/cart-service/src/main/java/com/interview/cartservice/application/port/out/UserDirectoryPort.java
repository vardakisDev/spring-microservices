package com.interview.cartservice.application.port.out;

import java.util.UUID;

public interface UserDirectoryPort {

    UserSnapshot getUser(UUID userId);

    record UserSnapshot(UUID id, boolean vip) {
    }
}