package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.GoogleUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface GoogleUserRepository extends JpaRepository<GoogleUser, String> {
  Optional<GoogleUser> findByGoogleId(String googleId);
}
