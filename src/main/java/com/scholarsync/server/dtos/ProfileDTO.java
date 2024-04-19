package com.scholarsync.server.dtos;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDTO {

  public ProfileDTO() {}

  private String id;
  private String username;
  private String firstName;
  private String lastName;
  private LocalDate birthDate;
  private LocalDateTime createdAt;
  private int credits;
  private List<Map<String, Object>> friends;
  private List<Map<String, Object>> groups;
}
