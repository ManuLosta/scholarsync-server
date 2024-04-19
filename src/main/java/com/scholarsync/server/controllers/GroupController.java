package com.scholarsync.server.controllers;

import com.scholarsync.server.services.GroupService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

  @Autowired private GroupService groupService;

  @PostMapping("/create")
  public ResponseEntity<Object> createGroup(@RequestBody Map<String, Object> group) {
    return groupService.createGroup(group);
  }

  @GetMapping("/getGroups")
  public ResponseEntity<Object> getGroups(@RequestParam(name = "user_id") String id) {
    return groupService.getGroups(id);
  }

  @GetMapping("/getGroup")
  public ResponseEntity<Object> getGroup(@RequestParam(name = "group_id") String id) {
    return groupService.getGroup(id);
  }

  @PostMapping("/remove-user-from-group")
  public ResponseEntity<Object> removeUserFromGroup(@RequestBody Map<String, String> group) {
    return groupService.removeUserFromGroup(group);
  }

}
