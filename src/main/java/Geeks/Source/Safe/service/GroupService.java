package Geeks.Source.Safe.service;

import Geeks.Source.Safe.Entity.*;

import Geeks.Source.Safe.Entity.Enum.FileStatus;
import Geeks.Source.Safe.Entity.Enum.InvitationStatus;
import Geeks.Source.Safe.Entity.Enum.RequestStatus;
import Geeks.Source.Safe.exceptions.UnauthorizedException;
import Geeks.Source.Safe.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupService {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private InvitationRepository invitationRepository;

    @Autowired
    private TextFileRepository fileRepository;

    @Autowired
    private FileRequestRepository fileRequestRepository;

    // Create a new group
    public Group createGroup(UUID creatorId, String groupName) {
        User creator = userRepository.findById(creatorId).orElseThrow(() -> new IllegalArgumentException("Creator not found"));
        Group group = Group.builder().name(groupName).creator(creator).build();
        return groupRepository.save(group);
    }

    // Search for users by name or username
    public List<User> searchUsers(String searchTerm) {
        return userRepository.findByUserNameContainingIgnoreCaseOrFullNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    // Send an invitation to a user
    public Invitation sendInvitation(UUID groupId, UUID invitedUserId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User invitedUser = userRepository.findById(invitedUserId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Invitation invitation = Invitation.builder()
                .group(group)
                .invitedUser(invitedUser)
                .status(InvitationStatus.PENDING)
                .build();

        return invitationRepository.save(invitation);
    }

    // Accept or reject an invitation
    public void respondToInvitation(UUID invitationId, InvitationStatus status) {
        Invitation invitation = invitationRepository.findById(invitationId).orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        invitation.setStatus(status);
        invitationRepository.save(invitation);

        if (status == InvitationStatus.ACCEPTED) {
            Group group = invitation.getGroup();
            group.getMembers().add(invitation.getInvitedUser());
            groupRepository.save(group);
        }
    }


    // Add a new file by the creator directly
    public File addFile(UUID groupId, UUID userId, String fileName, String extension, byte[] content) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User creator = group.getCreator();

        if (!creator.getId().equals(userId)) {
            throw new UnauthorizedException("Only the group creator can add files directly.");
        }

        File file = File.builder()
                .fileName(fileName)
                .extension(extension)
                .content(content)
                .group(group)
                .reservationStatus(FileStatus.FREE)
                .build();
        return fileRepository.save(file);
    }

    // Delete a file by the creator
    public void deleteFile(UUID groupId, UUID userId, UUID fileId) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found"));
        if (!group.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("Only the group creator can delete files.");
        }

        File file = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found"));
        fileRepository.delete(file);
    }

    // Update file by creator
    public File updateFile(UUID groupId, UUID userId, UUID fileId, String newFileName, byte[] newContent) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found"));
        if (!group.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("Only the group creator can update files.");
        }

        File file = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found"));
        file.setFileName(newFileName);
        file.setContent(newContent);
        return fileRepository.save(file);
    }



    // Create a file request by a member
    public FileRequest requestFileAddition(UUID groupId, UUID memberId, String fileName, byte[] content) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User requester = userRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (group.getCreator().getId().equals(requester.getId())) {
            throw new IllegalArgumentException("Creator does not need to request file addition.");
        }

        FileRequest fileRequest = FileRequest.builder()
                .group(group)
                .requester(requester)
                .fileName(fileName)
                .content(content)
                .status(RequestStatus.PENDING)
                .build();

        return fileRequestRepository.save(fileRequest);
    }

    // Approve or reject file request by creator
    public FileRequest handleFileRequest(UUID requestId, UUID approverId, RequestStatus status) {
        FileRequest request = fileRequestRepository.findById(requestId).orElseThrow(() -> new IllegalArgumentException("Request not found"));
        if (!request.getGroup().getCreator().getId().equals(approverId)) {
            throw new UnauthorizedException("Only the creator can approve or reject file requests.");
        }

        request.setStatus(status);
        if (status == RequestStatus.APPROVED) {
            File file = File.builder()
                    .fileName(request.getFileName())
                    .content(request.getContent())
                    .extension("test")
                    .group(request.getGroup())
                    .reservationStatus(FileStatus.FREE)
                    .build();
            fileRepository.save(file);
        }

        return fileRequestRepository.save(request);
    }

}
