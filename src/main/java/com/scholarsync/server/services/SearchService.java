package com.scholarsync.server.services;

import com.scholarsync.server.dtos.ProfileDTO;
import com.scholarsync.server.dtos.QuestionDTO;
import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.Question;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.GroupRepository;
import com.scholarsync.server.repositories.QuestionRepository;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupService groupService;

    public Object doSearch(String type, String text) {

        switch (type) {
            case "question":
                return searchQuestions(text);
            case "user":
                return searchUsers(text);
            case "group":
                return searchGroups(text);
            default:
                throw new IllegalArgumentException("Tipo de búsqueda no válido: " + type);
        }
    }

    private List<QuestionDTO> searchQuestions(String text) {
        List<QuestionDTO> questions = new ArrayList<>();
        for (Question question :  questionRepository.findByContentContainingOrTitleContainingOrAuthorFirstNameContainingOrAuthorLastNameContainingOrAuthorUsernameContaining(text, text, text, text, text)){
            questions.add(QuestionDTO.questionToDTO(question));

        }
        return questions;

    }


    private List<Map<String, Object>> searchGroups(String text) {
        List<Map<String, Object>> response = new ArrayList<>();
        Group[] foundGroups = groupRepository.findByTitleContainingOrDescriptionContaining(text, text);

        for (Group group : foundGroups) {
            Map<String, Object> groupMap = new HashMap<>();
            GroupService.createGroup(group, groupMap);
            response.add(groupMap);
        }

        return response;
    }

    private List<ProfileDTO> searchUsers(String text) {
        List<ProfileDTO> userList = new ArrayList<>();
        for (User user :  userRepository.findByFirstNameContainingOrLastNameContainingOrUsernameContaining(text, text, text)){
            userList.add(ProfileDTO.userToProfileDTO(user));
        }

        return userList;

    }


}
