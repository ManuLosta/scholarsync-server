package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Answer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerRepository extends JpaRepository<Answer, String> {}
