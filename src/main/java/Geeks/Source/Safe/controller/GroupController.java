package Geeks.Source.Safe.controller;

import Geeks.Source.Safe.Entity.*;
import Geeks.Source.Safe.Entity.Enum.InvitationStatus;
import Geeks.Source.Safe.Entity.Enum.RequestStatus;
import Geeks.Source.Safe.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;


    @PostMapping("/create")
    public Group createGroup(@RequestParam UUID creatorId, @RequestParam String groupName) {
        return groupService.createGroup(creatorId, groupName);
    }


    @GetMapping("/search-users")
    public List<User> searchUsers(@RequestParam String searchTerm) {
        return groupService.searchUsers(searchTerm);
    }


        @PostMapping("/{groupId}/invite")
        public Invitation sendInvitation(@PathVariable UUID groupId, @RequestParam UUID invitedUserId) {
            return groupService.sendInvitation(groupId, invitedUserId);
        }


    @PostMapping("/respond-invitation")
    public void respondToInvitation(@RequestParam UUID invitationId, @RequestParam InvitationStatus status) {
        groupService.respondToInvitation(invitationId, status);
    }


    // Add file by creator
    @PostMapping("/{groupId}/add-file")
    public File addFile(@PathVariable UUID groupId, @RequestParam UUID userId, @RequestParam String fileName, @RequestParam String extension, @RequestParam byte[] content) {
        return groupService.addFile(groupId, userId, fileName, extension, content);
    }

    // Delete file by creator
    @DeleteMapping("/{groupId}/delete-file/{fileId}")
    public void deleteFile(@PathVariable UUID groupId, @RequestParam UUID userId, @PathVariable UUID fileId) {
        groupService.deleteFile(groupId, userId, fileId);
    }

    // Update file by creator
    @PutMapping("/{groupId}/update-file/{fileId}")
    public File updateFile(@PathVariable UUID groupId, @RequestParam UUID userId, @PathVariable UUID fileId, @RequestParam String newFileName, @RequestParam byte[] newContent) {
        return groupService.updateFile(groupId, userId, fileId, newFileName, newContent);
    }

    // Request file addition by member
    @PostMapping("/{groupId}/request-file")
    public FileRequest requestFileAddition(@PathVariable UUID groupId, @RequestParam UUID memberId, @RequestParam String fileName, @RequestParam byte[] content) {
        return groupService.requestFileAddition(groupId, memberId, fileName, content);
    }

    // Approve or reject file request by creator
    @PostMapping("/handle-file-request")
    public FileRequest handleFileRequest(@RequestParam UUID requestId, @RequestParam UUID approvedId, @RequestParam RequestStatus status) {
        return groupService.handleFileRequest(requestId, approvedId, status);
    }
}
