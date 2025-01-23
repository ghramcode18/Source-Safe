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
        String newFilePath = storeFileOnDisk(modifiedFile,file.getFileName(),file.getVersion());

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
    private String storeFileOnDisk(MultipartFile file,String name ,int version ) throws IOException {
        if (!file.getName().equals(name))
        {
            throw new IllegalArgumentException("the file should be the same name you check-in");
        }
        // Generate unique file name (use the timestamp and original file name)
        String fileName = System.currentTimeMillis() + "v_" +version+"_"+ file.getOriginalFilename() ;
        Path filePath = Paths.get(fileStorageLocation, fileName);
        file.getSize();
        // Create directories if they don't exist
        Files.createDirectories(filePath.getParent());

        // Save file to disk
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(file.getBytes());
        }

        return filePath.toString(); // Return file path to store in DB
    }

    @Transactional
    public ResponseEntity<String> checkInFilesAndRedirect(List<UUID> fileIds, String username, String token) throws IOException {
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        for (UUID fileId : fileIds) {
            File file = fileRepository.findById(fileId)
                    .orElseThrow(() -> new IllegalArgumentException("File not found"));

            if (!FileStatus.FREE.equals(file.getReservationStatus())) {
                throw new IllegalStateException("File " + fileId + " is already reserved.");
            }

            file.setReservationStatus(FileStatus.RESERVED);
            file.setReservedBy(user);
            fileRepository.save(file);

            FileLog log = new FileLog(file, user, "Check-in");
            fileLogRepository.save(log);
        }

        // Redirect the user to the download endpoint
        return ResponseEntity
                .status(HttpStatus.FOUND)
                .header(HttpHeaders.LOCATION, "/files/download?fileIds=" + String.join(",", fileIds.toString()) + "&token=" + token)
                .body("Files reserved successfully! You will be redirected to download them.");
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

    @Transactional
    public File uploadFile(String groupName, MultipartFile file, String fileName, String username) throws IOException {
        // Ensure the user exists and is valid
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Ensure the group exists, and if not, throw an exception
        Group group = groupRepository.findByName(groupName)
                .orElseThrow(() -> new IllegalArgumentException("Group with the name '" + groupName + "' not found"));


        // Store the file content on disk and get the file path
        String filePath = storeFileOnDisk(file,file.getName(),0);

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

}
