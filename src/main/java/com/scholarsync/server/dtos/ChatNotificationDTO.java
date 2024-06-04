package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Group;

import java.time.LocalDateTime;

public record ChatNotificationDTO(String chat_id, LocalDateTime created_at, String name, String group) {
}
