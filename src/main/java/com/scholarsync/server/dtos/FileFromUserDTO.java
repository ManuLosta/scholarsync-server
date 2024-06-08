package com.scholarsync.server.dtos;

import java.time.LocalDateTime;

public record FileFromUserDTO(FileDTO file, ProfileDTO sender, LocalDateTime time) {

    static public FileFromUserDTO fromUserToServer(FileDTO file, ProfileDTO sender) {
        return new FileFromUserDTO(file, sender, LocalDateTime.now());
    }
}
