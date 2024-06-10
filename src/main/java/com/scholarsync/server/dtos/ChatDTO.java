package com.scholarsync.server.dtos;

import com.scholarsync.server.entities.Chat;
import com.scholarsync.server.entities.Group;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
public class ChatDTO {

  private String id;

  private String name;

  private String groupId;

  private String groupTitle;

  private Set<ProfileDTO> members;


  public static ChatDTO fromEntity(Chat chat) {
    ChatDTO chatDTO = new ChatDTO();
    chatDTO.setId(chat.getId());
    chatDTO.setName(chat.getName());
    chatDTO.setGroupId(chat.getGroup().getId());
    chatDTO.setGroupTitle(chat.getGroup().getTitle());
    chatDTO.setMembers(chat.getUsers().stream().map(ProfileDTO::userToProfileDTO).collect(Collectors.toSet()));
    return chatDTO;
  }

}
