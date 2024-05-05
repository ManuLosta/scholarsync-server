package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.AnswerFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerFileRepository extends JpaRepository<AnswerFiles, String> {}
