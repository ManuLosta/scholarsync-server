package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Question;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionRepository extends JpaRepository<Question, String> {

  Set<Question> findQuestionsByTitleContaining(String title);
}
