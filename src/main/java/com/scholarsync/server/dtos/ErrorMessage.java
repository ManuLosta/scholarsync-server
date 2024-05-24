package com.scholarsync.server.dtos;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {

  private String message;

  private String error;
}
