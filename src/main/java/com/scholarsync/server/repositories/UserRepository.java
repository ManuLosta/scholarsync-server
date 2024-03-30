package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.User;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByFirstNameAndLastName(String firstName, String lastName);
}