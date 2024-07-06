package com.scholarsync.server.dtos;

import java.time.LocalDateTime;
import java.util.Optional;

public record EventInputDTO(String title, LocalDateTime start, LocalDateTime end, String userId, String groupId, Optional<String> googleId) {
}
