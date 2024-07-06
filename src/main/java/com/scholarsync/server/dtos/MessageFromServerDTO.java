package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;



public record MessageFromServerDTO (String message, ProfileDTO sender, LocalDateTime time){


  static public MessageFromServerDTO fromUserToServer(MessageFromUserDTO message, ProfileDTO sender){
    return new MessageFromServerDTO(message.message(), sender, LocalDateTime.now());
  }

  static public MessageFromServerDTO fromAnonymousToServer(MessageFromAnonymousDTO message, ProfileDTO sender){
    return new MessageFromServerDTO(message.message(), sender, LocalDateTime.now());
  }
}
