package com.scholarsync.server.services;

import com.scholarsync.server.entities.Group;
import com.scholarsync.server.entities.User;
import com.scholarsync.server.repositories.GroupRepository;
import com.scholarsync.server.repositories.UserRepository;
import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class GroupService {

  @Autowired private GroupRepository groupRepository;
  @Autowired private UserRepository userRepository;

  private static List<Map<String, Object>> getGroupList(User user) {
    Set<Group> groups = user.getGroups();
    List<Map<String, Object>> response = new ArrayList<>();
    for (Group group : groups) {
      Map<String, Object> groupMap = new HashMap<>();
      createGroup(group, groupMap);
      response.add(groupMap);
    }
    return response;
  }

  private static void createGroup(Group group, Map<String, Object> groupMap) {
    groupMap.put("id", group.getId());
    groupMap.put("title", group.getTitle());
    groupMap.put("description", group.getDescription());
    groupMap.put("isPrivate", group.isPrivate());
    groupMap.put("createdBy", group.getCreatedBy().getId());
  }

  /**
   * Método createGroup
   *
   * <p>Este método se encarga de crear un nuevo grupo en la base de datos.
   *
   * @param group Es un Map que contiene la información necesaria para crear el grupo. Los campos
   *     que debe contener son:<br>
   *     - "title" <br>
   *     - "description"<br>
   *     - "isPrivate"<br>
   *     - "userId"<br>
   * @return ResponseEntity<Object> Este método retorna un objeto ResponseEntity que puede contener
   *     diferentes estados HTTP:<br>
   *     - HttpStatus.OK (200): Si el grupo se creó con éxito. El cuerpo de la respuesta será "Group
   *     Generated".<br>
   *     - HttpStatus.NOT_FOUND (404): Si el ID del usuario proporcionado no se encuentra en la base
   *     de datos. El cuerpo de la respuesta será "user/not-found".<br>
   *     - HttpStatus.BAD_REQUEST (400): Si el título del grupo ya está en uso. El cuerpo de la
   *     respuesta será "group/title-already-in-use".<br>
   *     - HttpStatus.BAD_REQUEST (400): Para cualquier otro error no especificado.<br>
   * @throws DataIntegrityViolationException Esta excepción se lanza si se viola alguna restricción
   *     de integridad de la base de datos, como por ejemplo, si el título del grupo ya está en uso.
   */
  public ResponseEntity<Object> createGroup(Map<String, Object> group) {
    try {
      Group generatedGroup = new Group();
      generatedGroup.setTitle((String) group.get("title"));
      generatedGroup.setDescription((String) group.get("description"));
      generatedGroup.setPrivate((Boolean) group.get("isPrivate"));
      Optional<User> optionalCreator = userRepository.findById((String) group.get("userId"));
      if (optionalCreator.isEmpty()) {
        return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
      } else {
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

    } catch (DataIntegrityViolationException e) {
      String errorMessage = e.getMostSpecificCause().getMessage();
      if (errorMessage.contains("title")) {
        String response = "group/title-already-in-use";
        System.out.println(response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
      } else {
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
      }
    }
  }

  /**
   * Método getGroups
   *
   * <p>Este método se encarga de obtener los grupos a los que pertenece un usuario.
   *
   * @param id Es el ID del usuario del que se quieren obtener los grupos.
   * @return ResponseEntity<Object> Este método retorna un objeto ResponseEntity que puede contener
   *     diferentes estados HTTP:<br>
   *     - HttpStatus.OK (200): Si el usuario se encuentra en la base de datos. El cuerpo de la
   *     respuesta será un Set de objetos Group.<br>
   *     - HttpStatus.NOT_FOUND (404): Si el ID del usuario proporcionado no se encuentra en la base
   *     de datos. El cuerpo de la respuesta será "user/not-found".<br>
   */
  public ResponseEntity<Object> getGroups(String id) {
    Optional<User> optionalUser = userRepository.findById(id);
    if (optionalUser.isEmpty()) {
      return new ResponseEntity<>("user/not-found", HttpStatus.NOT_FOUND);
    } else {
      User user = optionalUser.get();
      List<Map<String, Object>> response = getGroupList(user);
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
  }

  /**
   * Método getGroup
   *
   * <p>Este método se encarga de obtener un grupo en específico.
   *
   * @param id Es el ID del grupo que se quiere obtener.
   * @return ResponseEntity<Object> Este método retorna un objeto ResponseEntity que puede contener
   *     diferentes estados HTTP:<br>
   *     - HttpStatus.OK (200): Si el grupo se encuentra en la base de datos. El cuerpo de la
   *     respuesta será un objeto Group.<br>
   *     - HttpStatus.NOT_FOUND (404): Si el ID del grupo proporcionado no se encuentra en la base
   *     de datos. El cuerpo de la respuesta será "group/not-found".<br>
   */
  public ResponseEntity<Object> getGroup(String id) {
    Optional<Group> optionalGroup = groupRepository.findById(id);
    if (optionalGroup.isEmpty()) {
      return new ResponseEntity<>("group/not-found", HttpStatus.NOT_FOUND);
    } else {
      Group group = optionalGroup.get();
      Map<String, Object> response = new HashMap<>();
      createGroup(group, response);
      Set<User> users = group.getUsers();
      List<Map<String, Object>> usersList = new ArrayList<>();
      for (User user : users) {
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("user_id", user.getId());
        userMap.put("firstName", user.getFirstName());
        userMap.put("lastName", user.getLastName());
        userMap.put("username", user.getUsername());
        usersList.add(userMap);
      }
      response.put("users", usersList);
      return new ResponseEntity<>(response, HttpStatus.OK);
    }
  }

  public void addUserToGroup(Group invitedTo, User notified) {
    Set<Group> userGroups = notified.getGroups();
    if (userGroups != null) {
      userGroups.add(invitedTo);
    } else {
      notified.setGroups(Set.of(invitedTo));
    } // add group to user
    Set<User> participants = invitedTo.getUsers();
    if (participants != null) {
      participants.add(notified);
    } else {
      invitedTo.setUsers(Set.of(notified));
    }
    invitedTo.setUsers(participants); // add user to group
    groupRepository.save(invitedTo); // update group
    userRepository.save(notified); // update user
  }
}
