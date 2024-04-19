package com.scholarsync.server.dtos;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RefreshDTO {
  private String id;
  private String username;
  private String firstName;
  private String lastName;
}
