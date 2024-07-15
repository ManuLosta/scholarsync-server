package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GroupRepository extends JpaRepository<Group, String> {
    Group[] findByTitleContainingOrDescriptionContaining(String text, String text1);

    Optional<Group> findByTitle(String title);
}
