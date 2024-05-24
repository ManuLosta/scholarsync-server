package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.ChatFile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatFileRepository extends JpaRepository<ChatFile,String> {

  void deleteByChatId(String chatId);
}
