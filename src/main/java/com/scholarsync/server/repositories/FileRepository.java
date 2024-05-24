package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepository extends JpaRepository<File, String> {}
