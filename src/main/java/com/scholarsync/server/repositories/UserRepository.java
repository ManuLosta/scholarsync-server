package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    User findByEmail(String email);
    Optional<User> findById(String id);
}