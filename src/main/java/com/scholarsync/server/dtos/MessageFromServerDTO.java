package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Optional;


@Getter
@Setter
public class MessageFromServerDTO {

  String message;

  User sender;

  LocalDateTime time;

  static public MessageFromServerDTO fromUserToServer(MessageFromUserDTO message, User sender){
    MessageFromServerDTO messageFromServerDTO = new MessageFromServerDTO();
    messageFromServerDTO.setMessage(message.getMessage());
    messageFromServerDTO.setTime(LocalDateTime.now());
    messageFromServerDTO.setSender(sender);
    return messageFromServerDTO;
  }
}
