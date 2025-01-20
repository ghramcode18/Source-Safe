package Geeks.Source.Safe.service;

import Geeks.Source.Safe.Entity.Enum.FileStatus;
import Geeks.Source.Safe.Entity.File;
import Geeks.Source.Safe.Entity.FileLog;
import Geeks.Source.Safe.Entity.User;
import Geeks.Source.Safe.Entity.UserLog;
import Geeks.Source.Safe.repo.FileLogRepository;
import Geeks.Source.Safe.repo.TextFileRepository;
import Geeks.Source.Safe.repo.UserLogRepository;
import Geeks.Source.Safe.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

        private final TextFileRepository fileRepository;
        private final UserRepository userRepository;
        private final FileLogRepository fileLogRepository;

        private final UserLogRepository userLogRepository;


        public FileService(TextFileRepository fileRepository, UserRepository userRepository, FileLogRepository fileLogRepository, UserLogRepository userLogRepository) {
            this.fileRepository = fileRepository;
            this.userRepository = userRepository;
            this.fileLogRepository = fileLogRepository;
            this.userLogRepository = userLogRepository;
        }

    // File storage location (update this path based on your server)
    private final String fileStorageLocation = "/path/to/file/storage/";


    private String storeFileOnDisk(MultipartFile file) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = Paths.get(fileStorageLocation, fileName);
        Files.createDirectories(filePath.getParent());
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            fos.write(file.getBytes());
        }
        return filePath.toString();
    }

    @Transactional
    public List<File> checkInFiles(List<UUID> fileIds, UUID userId, List<MultipartFile> modifiedFiles) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<File> files = fileRepository.findAllById(fileIds);

        // Ensure all files are free before check-in
        for (File file : files) {
            if (!FileStatus.FREE.equals(file.getReservationStatus())) {
                throw new IllegalStateException("One or more files are not Free.");
            }
        }

        // Process files
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            MultipartFile modifiedFile = modifiedFiles.get(i);

            // Mark the file as reserved and store the file
            file.setReservationStatus(FileStatus.RESERVED);
            file.setReservedBy(user);
            String filePath = storeFileOnDisk(modifiedFile);
            file.setFilePath(filePath);

            // Save updated file state
            fileRepository.save(file);

            // Log the check-in action
            FileLog log = new FileLog(file, user, "Check-in");
            fileLogRepository.save(log);
        }

        return files;
    }

    // FileService.java
    public File getFileById(UUID fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
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
