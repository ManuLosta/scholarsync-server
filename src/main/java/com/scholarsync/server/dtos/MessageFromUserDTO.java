package com.scholarsync.server.dtos;


public record MessageFromUserDTO(String message, String sender_id, String chat_id) {}
