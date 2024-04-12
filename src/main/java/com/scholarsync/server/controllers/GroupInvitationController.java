package com.scholarsync.server.controllers;

import com.scholarsync.server.services.GroupInvitationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/group-invitations")
public class GroupInvitationController {
    @Autowired
    private GroupInvitationService groupInvitationService;

    @PostMapping("/send-invitation")
    public ResponseEntity<Object> sendInvitation(@RequestBody Map<String, Object> groupInvitationBody) {
        return groupInvitationService.sendGroupInvitation(groupInvitationBody);
    }

    @PostMapping("/accept-invitation")
    public ResponseEntity<Object> acceptInvitation(@RequestBody Map<String, String> groupInvitationBody) {
        return groupInvitationService.acceptGroupInvitation(groupInvitationBody.get("group_invitation_id"));
    }

    @PostMapping("/reject-invitation")
    public ResponseEntity<Object> rejectInvitation(@RequestBody Map<String, String> groupInvitationBody) {
        return groupInvitationService.declineGroupInvitation(groupInvitationBody.get("group_invitation_id"));
    }

    @GetMapping("/get-invitations/{userId}")
    public ResponseEntity<Object> getInvitations(@PathVariable String userId) {
        return groupInvitationService.getAllGroupInvitations(userId);
    }
}
