package Geeks.Source.Safe.service;

import Geeks.Source.Safe.Entity.FileLog;
import Geeks.Source.Safe.Entity.Group;
import Geeks.Source.Safe.Entity.User;
import Geeks.Source.Safe.Entity.UserLog;
import Geeks.Source.Safe.repo.FileLogRepository;
import Geeks.Source.Safe.repo.GroupRepository;
import Geeks.Source.Safe.repo.UserLogRepository;
import Geeks.Source.Safe.repo.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LogService {

    private final FileLogRepository fileLogRepository;
    private final UserLogRepository userLogRepository;
    private final GroupRepository groupRepository;
    private final UserRepository userRepository;

    public LogService(FileLogRepository fileLogRepository, UserLogRepository userLogRepository,
                      GroupRepository groupRepository, UserRepository userRepository) {
        this.fileLogRepository = fileLogRepository;
        this.userLogRepository = userLogRepository;
        this.groupRepository = groupRepository;
        this.userRepository = userRepository;
    }

    // Fetch file logs accessible to all group members
    public List<FileLog> getFileLogs(UUID fileId, UUID userId) {
        Group group = (Group) fileLogRepository.findGroupByFileId(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!group.getMembers().contains(user)) {
            throw new SecurityException("User is not a member of the group.");
        }

        return fileLogRepository.findByFileId(fileId);
    }

    // Fetch user logs accessible only by the group creator
    public List<UserLog> getUserLogs(UUID groupId, UUID creatorId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found"));

        if (!group.getCreator().getId().equals(creatorId)) {
            throw new SecurityException("Only the group creator can access user logs.");
        }

        return userLogRepository.findByGroupId(groupId);
    }
}
