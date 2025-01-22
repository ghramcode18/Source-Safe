package Geeks.Source.Safe.service;

import Geeks.Source.Safe.DTO.FileUploadDTO;
import Geeks.Source.Safe.Entity.*;
import Geeks.Source.Safe.Entity.Enum.FileStatus;
import Geeks.Source.Safe.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileOutputStream;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {
        @Autowired
         private  TextFileRepository fileRepository;
        @Autowired
        private  UserRepository userRepository;
        @Autowired
        private  FileLogRepository fileLogRepository;

        private  UserLogRepository userLogRepository;

        @Autowired
        private  GroupService groupService ;

        @Autowired
        private  GroupRepository groupRepository;

    // File storage location (update this path based on your server)
    private final String fileStorageLocation = "src/main/resources/files/";


    @Transactional
    public File checkOutFileAndUpload(UUID fileId, String username, MultipartFile modifiedFile) throws IOException {
        // Fetch user
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Fetch the file by ID
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        // Ensure the file is RESERVED before checkout
        if (!FileStatus.RESERVED.equals(file.getReservationStatus())) {
            throw new IllegalStateException("The file is not reserved or has already been checked out.");
        }

        // Store the modified file on disk and get the file path
        String newFilePath = storeFileOnDisk(modifiedFile);

        // Update file details: file path, version, and status
        file.setFilePath(newFilePath); // Update the file path with the modified file
        file.setReservationStatus(FileStatus.FREE); // Change the status to FREE
        file.setReservedBy(null); // Clear the reserved user

        // Increment file version if needed (you can also handle versioning differently)
        file.setVersion(file.getVersion() + 1);

        // Save the updated file to the database
        File updatedFile = fileRepository.save(file);

        // Log the checkout action
        FileLog log = new FileLog(updatedFile, user, "Check-out");
        fileLogRepository.save(log);

        return updatedFile;
    }
    @Transactional
    public ResponseEntity<String> checkInFileAndRedirect(UUID fileId, String username, String token) throws IOException {
        // Fetch user and file (same as before)
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        // Check if the file is free before reserving it
        if (!FileStatus.FREE.equals(file.getReservationStatus())) {
            throw new IllegalStateException("The file is already reserved.");
        }

        // Mark the file as reserved
        file.setReservationStatus(FileStatus.RESERVED);
        file.setReservedBy(user);
        fileRepository.save(file);

        FileLog log = new FileLog(file, user, "Check-in");
        fileLogRepository.save(log);

        // Redirect the user to the download endpoint
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/files/download?fileId=" + fileId + "&token=" + token)  // Add token here if needed for security
                .body("File reserved successfully! You will be redirected to the file download.");
    }

//    @Transactional
//    public ResponseEntity<String> checkInFileAndRedirect(UUID fileId, String username) throws IOException {
//        // Fetch user
//        User user = userRepository.findByUserName(username)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        // Fetch the file by ID
//        File file = fileRepository.findById(fileId)
//                .orElseThrow(() -> new IllegalArgumentException("File not found"));
//
//        // Ensure the file is free before check-in
//        if (!FileStatus.FREE.equals(file.getReservationStatus())) {
//            throw new IllegalStateException("The file is already reserved.");
//        }
//
//        // Mark the file as reserved and update reservedBy
//        file.setReservationStatus(FileStatus.RESERVED);
//        file.setReservedBy(user);
//
//        // Save the file status change to the database
//        fileRepository.save(file);
//
//        // Log the check-in action
//        FileLog log = new FileLog(file, user, "Check-in");
//        fileLogRepository.save(log);
//
//        // Prepare file path for download
//        java.io.File fileToDownload = new java.io.File(file.getFilePath());
//        if (!fileToDownload.exists()) {
//            throw new IllegalArgumentException("File does not exist at the given path.");
//        }
//
//        // Here, you simply return a response with a success message.
//        // The file will be downloaded via the redirect.
//
//        return ResponseEntity
//                .status(HttpStatus.FOUND)  // 302 Redirect
//                .header(HttpHeaders.LOCATION, "file:///" + file.getFilePath())  // Redirect to the file path
//                .body("File reserved successfully! You will be redirected to the file download.");
//    }


    // Method to store the uploaded file on disk

    private String storeFileOnDisk(MultipartFile file) throws IOException {
        // Generate unique file name (use the timestamp and original file name)
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(fileStorageLocation, fileName);

        // Create directories if they don't exist
        Files.createDirectories(filePath.getParent());

        // Save file to disk
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(file.getBytes());
        }

        return filePath.toString(); // Return file path to store in DB
    }
    @Transactional
    public File uploadFile(String groupName, MultipartFile file, String fileName, String username) throws IOException {
        // Ensure the user exists and is valid
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure the group exists, and if not, throw an exception
        Group group = groupRepository.findByName(groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group with the name '" + groupName + "' not found"));

        // Store the file content on disk and get the file path
        String filePath = storeFileOnDisk(file);

        // Create a new file entity with metadata and the file path
        File fileEntity = new File();
        fileEntity.setFileName(fileName);
        fileEntity.setFilePath(filePath);
        fileEntity.setReservationStatus(FileStatus.FREE);
        fileEntity.setGroup(group);  // Set the group
        fileEntity.setReservedBy(null);  // No one has reserved it yet

        // Save the file to the database
        return fileRepository.save(fileEntity);
    }

//    @Transactional
//    public List<File> checkInFiles(List<UUID> fileIds, String username, List<MultipartFile> modifiedFiles) throws IOException {
//        User user = userRepository.findByUserName(username)
//                .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//        List<File> files = fileRepository.findAllById(fileIds);
//
//        // Ensure all files are free before check-in
//        for (File file : files) {
//            if (!FileStatus.FREE.equals(file.getReservationStatus())) {
//                throw new IllegalStateException("One or more files are not Free.");
//            }
//        }
//
//        // Process files
//        for (int i = 0; i < files.size(); i++) {
//            File file = files.get(i);
//            MultipartFile modifiedFile = modifiedFiles.get(i);
//
//            // Mark the file as reserved and store the file
//            file.setReservationStatus(FileStatus.RESERVED);
//            file.setReservedBy(user);
//            String filePath = storeFileOnDisk(modifiedFile);
//            file.setFilePath(filePath);
//
//            // Save updated file state
//            fileRepository.save(file);
//
//            // Log the check-in action
//            FileLog log = new FileLog(file, user, "Check-in");
//            fileLogRepository.save(log);
//        }
//
//        return files;
//    }

    // FileService.java
    public File getFileById(UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
    }

    public List <File>getAllFileInMyGroup(String groupName){
        Group group = groupRepository.findByName(groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group with the name '" + groupName + "' not found"));

        List<File>files = fileRepository.findAllByGroupId(group.getId());
        return files;

    }
    @Transactional
    public List<File> checkOutFiles(List<UUID> fileIds, UUID userId, List<MultipartFile> uploadedFiles) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<File> files = fileRepository.findAllById(fileIds);

        // Validate user reservation
        for (File file : files) {
            if (file.getReservationStatus() != FileStatus.RESERVED || !file.getReservedBy().equals(user)) {
                throw new IllegalStateException("This file is not reserved by you.");
            }
        }

        // Process files for check-out (upload and versioning)
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            MultipartFile uploadedFile = uploadedFiles.get(i);

            // Compare contents (skip if unchanged)
            boolean contentChanged = !Files.readAllBytes(Paths.get(file.getFilePath())).equals(uploadedFile.getBytes());

            if (contentChanged) {
                // Store the uploaded file and increment version
                String filePath = storeFileOnDisk(uploadedFile);
                file.setFilePath(filePath);
                file.setVersion(file.getVersion() + 1); // Increment version
            }

            // Set file status to FREE
            file.setReservationStatus(FileStatus.FREE);
            file.setReservedBy(null); // Remove reservation

            // Save updated file state
            fileRepository.save(file);

            // Log the check-out action
            FileLog log = new FileLog(file, user, "Check-out");
            fileLogRepository.save(log);
        }

        return files;
    }
    @Transactional
    public File reserveFile(UUID fileId, UUID userId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));

        if (!FileStatus.FREE.equals(file.getReservationStatus())) {
            throw new IllegalStateException("File is already reserved.");
        }

        file.setReservationStatus(FileStatus.RESERVED);
        fileRepository.save(file);

        // Log the reservation
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        FileLog log = FileLog.builder()
                .file(file)
                .user(user)
                .action("Reserve")
                .build();
        fileLogRepository.save(log);

        return file;
    }

}
