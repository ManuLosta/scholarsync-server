package com.scholarsync.server.dtos;

import java.time.LocalDate;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterDTO {
  private String firstName;
  private String lastName;
  private String email;
  private String username;
  private String password;
  private LocalDate birthDate;
}
