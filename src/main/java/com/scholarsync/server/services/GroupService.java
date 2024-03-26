package com.scholarsync.server.services;


import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.GroupRepository;
import com.scholarsync.server.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * Método createGroup
     *
     * Este método se encarga de crear un nuevo grupo en la base de datos.
     *
     * @param group Es un Map que contiene la información necesaria para crear el grupo. Los campos que debe contener son:<br>
     *              - "title" <br>
     *              - "description"<br>
     *              - "isPrivate"<br>
     *              - "userId"<br>
     *
     * @return ResponseEntity<Object> Este método retorna un objeto ResponseEntity que puede contener diferentes estados HTTP:<br>
     *                               - HttpStatus.OK (200): Si el grupo se creó con éxito. El cuerpo de la respuesta será "Group Generated".<br>
     *                               - HttpStatus.NOT_FOUND (404): Si el ID del usuario proporcionado no se encuentra en la base de datos. El cuerpo de la respuesta será "user/not-found".<br>
     *                               - HttpStatus.BAD_REQUEST (400): Si el título del grupo ya está en uso. El cuerpo de la respuesta será "group/title-already-in-use".<br>
     *                               - HttpStatus.BAD_REQUEST (400): Para cualquier otro error no especificado.<br>
     *
     * @throws DataIntegrityViolationException Esta excepción se lanza si se viola alguna restricción de integridad de la base de datos, como por ejemplo, si el título del grupo ya está en uso.
     */
    public ResponseEntity<Object> createGroup(Map<String,Object> group){
        try {
            Group generatedGroup = new Group();
            generatedGroup.setTitle((String) group.get("title"));
            generatedGroup.setDescription((String) group.get("description"));
            generatedGroup.setPrivate((Boolean) group.get("isPrivate"));
            Optional<User> optionalCreator = userRepository.findById((Long) group.get("userId"));
            if(optionalCreator.isEmpty()){
                return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
            }
            else{
                User creator = optionalCreator.get();
                generatedGroup.setCreatedBy(creator);
                Set<User> userSet = generatedGroup.getUsers();
                userSet.add(creator);
                generatedGroup.setUsers(userSet);
                generatedGroup.setCreatedBy(creator);
                groupRepository.save(generatedGroup);

                creator.getGroups().add(generatedGroup);
                userRepository.save(creator);


                System.out.println("Group " + generatedGroup.getTitle() + " created successfully!");
                return new ResponseEntity<>("Group Generated", HttpStatus.OK);
            }

        }
        catch (DataIntegrityViolationException e){
            String errorMessage = e.getMostSpecificCause().getMessage();
            if(errorMessage.contains("title")){
                String response = "group/title-already-in-use";
                System.out.println(response);
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
            else {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }


    }
}
