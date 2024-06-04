package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Files;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<Files, String> {}
