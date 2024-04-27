package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.QuestionFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionFileRepository extends JpaRepository<QuestionFiles,String> {

}
