package Geeks.Source.Safe.controller;

import Geeks.Source.Safe.Entity.*;
import Geeks.Source.Safe.Entity.Enum.InvitationStatus;
import Geeks.Source.Safe.Entity.Enum.RequestStatus;
import Geeks.Source.Safe.security.JwtUtil;
import Geeks.Source.Safe.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    JwtUtil jwtUtil;

    // Endpoint to add a new user
    @PostMapping("/adduser")
    public ResponseEntity<String> addUser(@RequestHeader("Authorization") String token, @RequestBody User userRequest) {
        // Remove "Bearer " prefix if present
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Extract roles from the token
        List<String> roles = jwtUtil.extractRoles(token);

        // Check if the user has the "ADMIN" role
        if (!roles.contains("ADMIN")) {
            return ResponseEntity.status(403).body("You do not have permission to add a user.");
        }

        // If the user has "ADMIN" role, proceed with adding the user
        String response = groupService.addUser(userRequest);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/create")
    public String createGroup(@RequestHeader("Authorization") String token, @RequestParam String groupName) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.extractUsername(token);
        return groupService.createGroup(username, groupName);
    }




    @GetMapping("/search-users")
    public List<User> searchUsers(@RequestHeader("Authorization") String token,@RequestParam String searchTerm) {
        return groupService.searchUsers(searchTerm);
    }


    @GetMapping("/get-users")
    public List<User> getAllUsers(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.extractUsername(token);
        return groupService.getUsersInSameGroupAsUser(username);
    }

    @GetMapping("/get-groups")
    public List<Group> getGroupsWhereUserIsMember(@RequestHeader("Authorization") String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.extractUsername(token);
        return groupService.getGroupsWhereUserIsMember(username);
    }

    @PostMapping("/invite")
        public Invitation sendInvitation(@RequestHeader("Authorization") String token,@RequestParam UUID groupId, @RequestParam UUID invitedUserId) {
            return groupService.sendInvitation(groupId, invitedUserId);
        }


    @PostMapping("/respond-invitation")
    public void respondToInvitation(@RequestParam UUID invitationId, @RequestParam InvitationStatus status) {
        groupService.respondToInvitation(invitationId, status);
    }


    // Add file by creator
    @PostMapping("/{groupId}/add-file")
    public File addFile(@PathVariable UUID groupId, @RequestParam UUID userId, @RequestParam String fileName, @RequestParam String extension, @RequestParam String content) {
        return groupService.addFile(groupId, userId, fileName, extension, content);
    }

    // Delete file by creator
    @DeleteMapping("/{groupId}/delete-file/{fileId}")
    public void deleteFile(@PathVariable UUID groupId, @RequestParam UUID userId, @PathVariable UUID fileId) {
        groupService.deleteFile(groupId, userId, fileId);
    }

    // Update file by creator
    @PutMapping("/{groupId}/update-file/{fileId}")
    public File updateFile(@PathVariable UUID groupId, @RequestParam UUID userId, @PathVariable UUID fileId, @RequestParam String newFileName, @RequestParam String newContent) {
        return groupService.updateFile(groupId, userId, fileId, newFileName, newContent);
    }

    // Request file addition by member
    @PostMapping("/{groupId}/request-file")
    public FileRequest requestFileAddition(@PathVariable UUID groupId, @RequestParam UUID memberId, @RequestParam String fileName, @RequestParam String content) {
        return groupService.requestFileAddition(groupId, memberId, fileName, content);
    }

    // Approve or reject file request by creator
    @PostMapping("/handle-file-request")
    public FileRequest handleFileRequest(@RequestParam UUID requestId, @RequestParam UUID approvedId, @RequestParam RequestStatus status) {
        return groupService.handleFileRequest(requestId, approvedId, status);
    }
}
