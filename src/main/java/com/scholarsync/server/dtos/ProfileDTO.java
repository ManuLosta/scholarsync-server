package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Getter
@Setter
public class ProfileDTO {

    public ProfileDTO(){}

    private String id;
    private String username;
    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    private LocalDateTime createdAt;
    private int credits;
    private List<Map<String,Object>> friends;
    private List<Map<String,Object>> groups;




}
