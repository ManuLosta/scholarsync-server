package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, Long> {
}
