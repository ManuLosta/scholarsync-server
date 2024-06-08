package com.scholarsync.server.dtos;

import java.time.LocalDateTime;

public record EventInputDTO(String title, LocalDateTime start, LocalDateTime end, String userId, String groupId) {
}
