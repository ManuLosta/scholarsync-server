package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
  Optional<User> findUserByEmail(String email);

  Optional<User> findUserByUsername(String username);

  Optional<User> findUserByFirstNameAndLastName(String firstName, String lastName);

  Optional<User> findUserById(String id);

    User[] findByFirstNameContainingOrLastNameContainingOrUsernameContaining(String text, String text1, String text2);
}
