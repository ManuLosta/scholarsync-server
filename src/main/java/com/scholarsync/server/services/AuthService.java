package com.scholarsync.server.services;

import com.scholarsync.server.dtos.UserDTO;
import com.scholarsync.server.entities.Session;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.SessionRepository;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class AuthService {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SessionRepository sessionRepository;

    public ResponseEntity<Object> register(User user) {
        try {
            userRepository.save(user);
            String response =  "User " + user.getFirstName() + " " + user.getLastName() + " registered successfully!";
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
        catch (DataIntegrityViolationException e){
            String errorMessage = e.getMostSpecificCause().getMessage();
            if (errorMessage.contains("email")) {
                String response =  "Email " + user.getEmail() + " already in use!";
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (errorMessage.contains("username")) {
                String response =  "Username " + user.getUsername() + " already in use!";
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else if (errorMessage.contains("email") & errorMessage.contains("username")) {
                String response =  "Email " + user.getEmail() + " and Username " + user.getUsername() + " already in use!";
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

            }
        }
    }

    public ResponseEntity<Object> login(UserDTO userDTO) {
        User user = convertToEntity(userDTO);

        if (user != null) {
            if (user.getPassword().equals(userDTO.getPassword())) {
                Session session = new Session();
                session.setUser(user);
                sessionRepository.save(session);
                return new ResponseEntity<>(session.getId(),HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Incorrect Password", HttpStatus.UNAUTHORIZED);
            }
        } else {
            return new ResponseEntity<>("User not Found", HttpStatus.NOT_FOUND);
        }
    }


    public User convertToEntity(UserDTO userDTO) {
        return userRepository.findByEmail(userDTO.getEmail());
    }
}
