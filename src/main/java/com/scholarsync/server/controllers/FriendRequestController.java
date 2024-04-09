package com.scholarsync.server.controllers;

import com.scholarsync.server.services.FriendRequestService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/friend-requests")
public class FriendRequestController {

  @Autowired FriendRequestService friendRequestService;

  @PostMapping("/send-friend-request")
  public ResponseEntity<Object> sendFriendRequest(
      @RequestBody Map<String, String> friendRequestBody) {
    return friendRequestService.sendFriendRequest(friendRequestBody);
  }

  @GetMapping("/{id}/friend-requests")
  public ResponseEntity<Object> getAllRequests(@PathVariable String id) {
    return friendRequestService.getAllRequests(id);
  }

  @PostMapping("accept-request")
  public ResponseEntity<Object> acceptFriendRequest(
      @RequestBody Map<String, String> friendRequestBody) {
    String id = friendRequestBody.get("id");
    return friendRequestService.acceptFriendRequest(id);
  }

  @PostMapping("deny-request")
  public ResponseEntity<Object> rejectFriendRequest(
      @RequestBody Map<String, String> friendRequestBody) {
    String id = friendRequestBody.get("id");
    return friendRequestService.deleteFriendRequest(id);
  }
}
