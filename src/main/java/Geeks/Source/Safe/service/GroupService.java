package Geeks.Source.Safe.service;

import Geeks.Source.Safe.Entity.*;

import Geeks.Source.Safe.Entity.Enum.FileStatus;
import Geeks.Source.Safe.Entity.Enum.InvitationStatus;
import Geeks.Source.Safe.Entity.Enum.RequestStatus;
import Geeks.Source.Safe.exceptions.UnauthorizedException;
import Geeks.Source.Safe.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

    public String addUser(User userRequest) {
        System.out.println(userRequest.toString());
        // Additional validation logic can go here if needed
        if (userRequest.getUserName() == null || userRequest.getUserName().isEmpty()) {
            return "Username is required.";
        }

        if (userRequest.getEmail() == null || userRequest.getEmail().isEmpty()) {
            return "Email is required.";
        }

        if (userRequest.getPassword() == null || userRequest.getPassword().isEmpty()) {
            return "Password is required.";
        }

        // Check if the email already exists
        Optional<User> existingUserByEmail = userRepository.findByEmail(userRequest.getEmail());
        if (existingUserByEmail.equals(null)) {
            return "Email is already taken, Please try again.";
        }

        // Check if the username already exists
        Optional<User> existingUserByUserName = userRepository.findByUserName(userRequest.getUserName());
        if (existingUserByUserName.equals(null)) {
            return "Username is already taken, Please try again.";
        }

        // Save the new user to the database
        userRepository.save(userRequest);

        return "The user added successfully " + userRequest.getUserName();
    }

    // Create a new group
    public String createGroup(String username, String groupName) {
        User creator = userRepository.findByUserName(username).orElseThrow(() -> new IllegalArgumentException("Creator not found"));
        Optional<Group> group1 = groupRepository.findByName(groupName);
        if (!group1.isPresent())
        {
            Group group = Group.builder().name(groupName).creator(creator).build();
            groupRepository.save(group);
            return  "the group create successfully "+ groupName;
        }
        return "the group name is already taken, please try again";
    }

    // Search for users by name or username
    public List<User> searchUsers(String searchTerm) {
        return userRepository.findByUserNameContainingIgnoreCaseOrFullNameContainingIgnoreCase(searchTerm, searchTerm);
    }

    public List <Group> getGroupsWhereUserIsMember(String username){
        Optional<User> user= userRepository.findByUserName(username);
        List<UUID> groupIds = groupRepository.findGroupsWhereUserIsMember(user.get().getId());
        List<Group>groups = groupRepository.findAllById(groupIds);
        return groups;

    }

    public List<User> getUsersInSameGroupAsUser(String username) {

        Optional<User> user= userRepository.findByUserName(username);
        // Step 1: Find the groups where the user is a member
        List<UUID> groupIds = groupRepository.findGroupsWhereUserIsMember(user.get().getId());

        if (groupIds.isEmpty()) {
            return new ArrayList<>();  // No groups found for the user
        }

        // Step 2: Find all users in those groups
        List<User>users= userRepository.findAllById(groupRepository.findUsersInGroups(groupIds));
        return users;
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
    public File addFile(UUID groupId, UUID userId, String fileName, String extension, String content) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found"));
        User creator = group.getCreator();

        if (!creator.getId().equals(userId)) {
            throw new UnauthorizedException("Only the group creator can add files directly.");
        }

        File file = File.builder()
                .fileName(fileName)
//                .extension(extension)
//                .content(content)
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
    public File updateFile(UUID groupId, UUID userId, UUID fileId, String newFileName, String newContent) {
        Group group = groupRepository.findById(groupId).orElseThrow(() -> new IllegalArgumentException("Group not found"));
        if (!group.getCreator().getId().equals(userId)) {
            throw new UnauthorizedException("Only the group creator can update files.");
        }

        File file = fileRepository.findById(fileId).orElseThrow(() -> new IllegalArgumentException("File not found"));
        file.setFileName(newFileName);
//        file.setContent(newContent);
        return fileRepository.save(file);
    }



    // Create a file request by a member
    public FileRequest requestFileAddition(UUID groupId, UUID memberId, String fileName, String content) {
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
//                    .content(request.getContent())
//                    .extension("test")
                    .group(request.getGroup())
                    .reservationStatus(FileStatus.FREE)
                    .build();
            fileRepository.save(file);
        }

        return fileRequestRepository.save(request);
    }

}
