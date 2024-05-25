package com.scholarsync.server.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Setter
@Getter
public class MessageFromUserDTO {

  String message;

  String senderId;

  String chatId;


}
