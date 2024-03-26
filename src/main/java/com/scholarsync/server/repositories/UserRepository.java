package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findById(long id);

}