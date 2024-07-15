package com.scholarsync.server.services;

import com.github.javafaker.Faker;
import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.*;
import com.scholarsync.server.types.levelType;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DbFillerService {

  @Autowired private UserRepository userRepository;
  @Autowired private GroupRepository groupRepository;
  @Autowired private QuestionRepository questionRepository;
  @Autowired private AnswerRepository answerRepository;
  @Autowired private RatingRepository ratingRepository;

  private static final Logger log = LoggerFactory.getLogger(DbFillerService.class);
  private static final Faker faker = new Faker(new java.util.Locale("es"), new Random(123456789L)); // Spanish locale for Faker
  private static final Random random = new Random(123456789L); // Fixed seed for Random

  @Transactional
  public void fillDatabase() {
    log.info("Filling database...");

    for (int i = 0; i < 100; i++) {
      User user = new User();
      user.setEmail(faker.internet().emailAddress());
      user.setUsername(faker.name().username());
      user.setPassword(faker.internet().password());
      user.setFirstName(faker.name().firstName());
      user.setLastName(faker.name().lastName());
      user.setBirthDate(LocalDate.now().minusYears(20 + random.nextInt(30)));
      user.setCredits(faker.number().numberBetween(5000, 100000));
      user.setXp(faker.number().numberBetween(100, 10000));
      user.setLevel(levelType.values()[random.nextInt(levelType.values().length)]);
      userRepository.save(user);

      String groupName = faker.educator().course();
      int attempt = 1;
      while (groupRepository.findByTitle(groupName).isPresent()) {
        groupName = faker.educator().course() + " " + attempt++;
      }

      Group group = new Group();
      group.setTitle(groupName);
      group.setDescription(faker.lorem().sentence());
      group.setPrivate(random.nextBoolean());
      group.setCreatedBy(user);
      group.setUsers(new HashSet<>());
      group.setCreatedAt(LocalDateTime.now().minusMonths(random.nextInt(60)));
      Set<User> userSet = group.getUsers();
      userSet.add(user);
      group.getUsers().forEach(u -> {
        if (random.nextBoolean()) userSet.add(u);
      });
      group.setUsers(userSet);
      groupRepository.save(group);
      if (user.getGroups() == null) user.setGroups(new HashSet<>());
      user.getGroups().add(group);
      userRepository.save(user);

      if (group.getUsers() == null) group.setUsers(new HashSet<>());
      for (User groupUser : group.getUsers()) {
        if (groupUser.equals(user)) continue;
        Set<Group> groupSet = groupUser.getGroups();
        groupSet.add(group);
        userRepository.save(groupUser);
      }

      Question question = new Question();
      question.setTitle("Question: " + faker.lorem().sentence());
      question.setContent(faker.lorem().paragraph());
      question.setAuthor(user);
      question.setGroup(group);
      question.setCreatedAt(LocalDateTime.now().minusDays(random.nextInt(30)));
      questionRepository.save(question);

      Answer answer = new Answer();
      answer.setContent(faker.lorem().paragraph());
      answer.setQuestion(question);
      answer.setUser(user);
      answer.setGroup(group);
      Rating rating = new Rating();
      rating.setRating(faker.number().randomDouble(2, 1, 5));
      rating.setAnswer(answer);
      rating.setUserId(user);
      if (answer.getRatings() == null) answer.setRatings(new HashSet<>());
      answer.getRatings().add(rating);
      ratingRepository.save(rating);
      userRepository.save(user);
      answerRepository.save(answer);
    }
    log.info("Finished filling database with consistent, real-looking data.");
  }
}
