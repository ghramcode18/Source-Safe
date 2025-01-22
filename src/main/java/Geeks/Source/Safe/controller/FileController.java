package Geeks.Source.Safe.controller;


import Geeks.Source.Safe.DTO.FileUploadDTO;
import Geeks.Source.Safe.Entity.File;
import Geeks.Source.Safe.Entity.Group;
import Geeks.Source.Safe.repo.TextFileRepository;
import Geeks.Source.Safe.repo.UserRepository;
import Geeks.Source.Safe.security.JwtUtil;
import Geeks.Source.Safe.service.FileService;

import jdk.jshell.execution.FailOverExecutionControlProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/files")
public class FileController {
    @Autowired
    JwtUtil jwtUtil;
    private final FileService fileService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    TextFileRepository fileRepository;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // Check-in and Download the File
    @PostMapping("/check-in")
    public ResponseEntity<String> checkInFileAndDownload(@RequestParam("fileId") UUID fileId,
                                                         @RequestParam("token") String token) throws IOException {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.extractUsername(token);
        return fileService.checkInFileAndRedirect(fileId, username, token);
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam("fileId") UUID fileId,
                                                 @RequestParam("token") String token) throws IOException {
        // Fetch the file by ID
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        // Ensure the file exists on the server
        java.io.File fileToDownload = new java.io.File(file.getFilePath());
        if (!fileToDownload.exists()) {
            throw new IllegalArgumentException("File not found on the server.");
        }

        // Serve the file as a resource
        Resource resource = new FileSystemResource(fileToDownload);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=" + file.getFileName());

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(fileToDownload.length())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    // Checkout and Upload the Modified File
    @PostMapping("/check-out")
    public ResponseEntity<File> checkOutFileAndUpload(@RequestParam UUID fileId,
                                                      @RequestHeader("Authorization") String token,
                                                      @RequestParam("file") MultipartFile modifiedFile) throws IOException {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.extractUsername(token);
        File updatedFile = fileService.checkOutFileAndUpload(fileId, username, modifiedFile);
        return ResponseEntity.ok(updatedFile);
    }



    @GetMapping("/get-files")
    public List<File> getAllFileInMyGroup(@RequestHeader("Authorization") String token,@RequestParam("groupName") String groupName) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.extractUsername(token);
        return fileService.getAllFileInMyGroup(groupName);
    }
    @PostMapping("/upload")
    public ResponseEntity<File> uploadFile(@RequestHeader("Authorization") String token,
                                           @RequestParam("groupName") String groupName,
                                           @RequestParam("file") MultipartFile file) throws IOException {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.extractUsername(token);
        String fileName = extractFileName(file);

        // Call the service to upload the file
        File savedFile = fileService.uploadFile(groupName, file, fileName, username);
        return ResponseEntity.ok(savedFile);
    }

    // Method to extract the file name from the file
    private String extractFileName(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new IllegalArgumentException("File name is missing");
        }

//        // Remove the extension (.csv, .json)
//        int extIndex = originalFileName.lastIndexOf('.');
//        if (extIndex != -1) {
//            return originalFileName.substring(0, extIndex);
//        }
        return originalFileName;
    }

    // Helper method to check if the user is an admin (you need to implement this logic based on your User entity)
//    private boolean checkIfUserIsAdmin(UUID userId) {
//        // Assume you have a method in your user service to check the role
//        // Example: return userService.isAdmin(userId);
//        return true; // Assume true for now, replace with actual check
//    }

//    @GetMapping("/download/{fileId}")
//    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable UUID fileId) {
//        File file = fileService.getFileById(fileId);
//        String filePath = file.getFilePath();
//        FileSystemResource resource = new FileSystemResource(new java.io.File(filePath));
//
//        if (!resource.exists()) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName());
//        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
//    }
}
