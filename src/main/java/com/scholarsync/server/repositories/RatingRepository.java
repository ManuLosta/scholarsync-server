package com.scholarsync.server.repositories;

import com.scholarsync.server.entities.Answer;
import com.scholarsync.server.entities.Rating;
import com.scholarsync.server.entities.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RatingRepository extends JpaRepository<Rating, String> {
  Optional<Rating> findByAnswerAndUserId(Answer answer, User userId);
}
