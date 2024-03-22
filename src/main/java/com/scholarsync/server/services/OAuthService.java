package com.scholarsync.server.services;

import com.scholarsync.server.DataTransferProtocols.UserDTO;
import com.scholarsync.server.entities.Session;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.SessionRepository;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class OAuthService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;

    public String register(User user) {

        userRepository.save(user);
        return "User" + user.getFirstName() + " " + user.getLastName() + " registered successfully!";
    }

    public long login(UserDTO userDTO) {
        User user = convertToEntity(userDTO);

        if (user != null) {
            if (user.getPassword().equals(userDTO.getPassword())) {
                Session session = new Session();
                session.setUser(user);
                sessionRepository.save(session);
                return session.getId();
            } else {
                return 401;
            }
        } else {
            return 404;
        }
    }


    public User convertToEntity(UserDTO userDTO) {
        return userRepository.findByEmail(userDTO.getEmail());
    }
}
