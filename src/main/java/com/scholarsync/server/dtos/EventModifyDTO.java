package com.scholarsync.server.dtos;

import java.time.LocalDateTime;

public record EventModifyDTO(String id, String title, LocalDateTime start, LocalDateTime end) {}
