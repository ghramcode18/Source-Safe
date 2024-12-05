package Geeks.Source.Safe.controller;

import Geeks.Source.Safe.Entity.*;
import Geeks.Source.Safe.Entity.Enum.InvitationStatus;
import Geeks.Source.Safe.Entity.Enum.RequestStatus;
import Geeks.Source.Safe.service.GroupService;
import Geeks.Source.Safe.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/logs")
public class LogController {

    private final LogService logService;

    public LogController(LogService logService) {
        this.logService = logService;
    }

    // API for file logs
    @GetMapping("/file")
    public ResponseEntity<List<FileLog>> getFileLogs(
            @RequestParam UUID fileId,
            @RequestParam UUID userId) {
        List<FileLog> fileLogs = logService.getFileLogs(fileId, userId);
        return ResponseEntity.ok(fileLogs);
    }

    // API for user logs
    @GetMapping("/user")
    public ResponseEntity<List<UserLog>> getUserLogs(
            @RequestParam UUID groupId,
            @RequestParam UUID creatorId) {
        List<UserLog> userLogs = logService.getUserLogs(groupId, creatorId);
        return ResponseEntity.ok(userLogs);
    }
}
