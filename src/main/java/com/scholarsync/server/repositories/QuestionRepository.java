package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface QuestionRepository extends JpaRepository<Question,String> {

    Set<Question> findQuestionsByTitleContaining(String title);
    Set<Question> findQuestionsByContentContaining(String content);
    Set<String> findTitlesByTitleContaining(String title);
}
