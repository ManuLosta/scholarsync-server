package com.scholarsync.server.services;

import com.scholarsync.server.entities.*;
import com.scholarsync.server.repositories.*;
import com.scholarsync.server.types.levelType;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

@Service
public class DbFillerService {

    @Autowired private UserRepository userRepository;
    @Autowired private GroupRepository groupRepository;
    @Autowired private QuestionRepository questionRepository;
    @Autowired private AnswerRepository answerRepository;

    private static final Logger log = LoggerFactory.getLogger(DbFillerService.class);

    @Transactional
    public void fillDatabase() {
        log.info("Filling database...");

        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            User user = new User();
            user.setEmail("user" + i + "@test.com");
            user.setUsername("user" + i);
            user.setPassword("password");
            user.setFirstName("First" + i);
            user.setLastName("Last" + i);
            user.setBirthDate(LocalDate.now().minusYears(20 + random.nextInt(30)));
            user.setCredits(100000);
            user.setXp(100);
            int levelOrdinalEnum = random.nextInt(10);
            user.setLevel(levelType.values()[levelOrdinalEnum]);
            userRepository.save(user);

            Group group = new Group();
            group.setTitle("Group" + i);
            group.setDescription("Description" + i);
            group.setPrivate(random.nextBoolean());
            group.setCreatedBy(user);
            group.setUsers(new HashSet<>());
            group.setCreatedAt(LocalDateTime.now().minusMonths(random.nextInt(60)));
            Set<User> userSet = group.getUsers();
            userSet.add(user);
            for (User randomUser: userRepository.findAll()) {
                if(randomUser.equals(user)) continue;
                if(random.nextBoolean()) userSet.add(randomUser);
            }
            group.setUsers(userSet);
            groupRepository.save(group);
            if(user.getGroups() == null) user.setGroups(new HashSet<>());
            user.getGroups().add(group);
            userRepository.save(user);
            groupRepository.save(group);


            if(group.getUsers() == null) group.setUsers(new HashSet<>());
            for(User groupUser: group.getUsers()) {
                if (groupUser.equals(user)) continue;
                Set<Group> groupSet = groupUser.getGroups();
                groupSet.add(group);
                userRepository.save(groupUser);
            }

            Question question = new Question();
            question.setTitle("Random Question Number: " + i);
            question.setContent("<p>Content Information on Question:" + i+"</p>");
            question.setAuthor(user);
            question.setGroup(group);
            LocalDateTime groupCreationDate = group.getCreatedAt();
            questionRepository.save(question);
            questionRepository.updateCreatedAt(question.getId(), groupCreationDate.plusMonths(random.nextInt(60)));

            Answer answer = new Answer();
            answer.setContent("Answer" + i);
            answer.setQuestion(question);
            answer.setUser(user);
            answer.setGroup(group);
            answer.setUpvotes(random.nextInt(1000));
            answer.setDownvotes(random.nextInt(1000));
            answerRepository.save(answer);
        }
        log.info("Finished filling database.");

    }


}