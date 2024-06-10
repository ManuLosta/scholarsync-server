package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Question;
import java.time.LocalDateTime;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface QuestionRepository extends JpaRepository<Question, String> {

  Set<Question> findQuestionsByTitleContaining(String title);

  @Modifying
  @Query(value = "UPDATE question SET created_at = :createdAt WHERE id = :id", nativeQuery = true)
  void updateCreatedAt(@Param("id") String id, @Param("createdAt") LocalDateTime createdAt);

  Question[] findByContentContainingOrTitleContainingOrAuthorFirstNameContainingOrAuthorLastNameContainingOrAuthorUsernameContaining(String text, String text1, String text2, String text3, String text4);
}
