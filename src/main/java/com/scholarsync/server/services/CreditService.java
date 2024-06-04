package com.scholarsync.server.services;

import com.scholarsync.server.entities.Answer;
import com.scholarsync.server.entities.Rating;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreditService {
    @Autowired UserRepository userRepository;

    public void awardAnswer(Answer answer, User rater, Rating rating) {
        int ratingCount = answer.getRatings().size();
        double ratingAverage = getAnswerRatingAverage(answer);

        User author = answer.getUser();
        User questionAuthor = answer.getQuestion().getAuthor();

        if (questionAuthor.equals(rater)) {
            giveCredits(author, (int) rating.getRating() * 5);
        }

        if (ratingCount == 5 || ratingCount == 10 || ratingCount == 20 || ratingCount == 50) {
            giveCredits(author, (int) ratingAverage * ratingCount);
        }
    }

    public void giveCredits(User user, int amount) {
        user.setCredits(user.getCredits() + amount);
        userRepository.save(user);
    }

    public double getAnswerRatingAverage(Answer answer) {
        return answer.getRatings().stream().mapToDouble(Rating::getRating).average().orElse(0);
    }
}
