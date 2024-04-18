package com.scholarsync.server.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginDTO {
  private String email;
  private String password;
}
