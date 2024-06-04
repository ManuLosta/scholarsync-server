package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Chat;
import com.scholarsync.server.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat,String> {

  boolean existsByNameAndGroupId(String name, String groupId);

  List<Chat> findByGroup(Group group);
}
