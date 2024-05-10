package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating,String> {
    Optional<Rating> findByAnswerIdAndUserId(String answerId, String userId);
}
