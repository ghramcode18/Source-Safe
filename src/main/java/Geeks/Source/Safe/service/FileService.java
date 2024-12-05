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
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

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

//'
//        // حجز ملف (Check-Out)
//        public File checkOutFile(UUID fileId, UUID userId) {
//            File file = fileRepository.findById(fileId)
//                    .orElseThrow(() -> new IllegalArgumentException("File not found"));
//
//            if (file.getReservationStatus() == FileStatus.RESERVED) {
//                throw new IllegalStateException("File is already reserved");
//            }
//
//            User user = userRepository.findById(userId)
//                    .orElseThrow(() -> new IllegalArgumentException("User not found"));
//
//            file.setReservationStatus(FileStatus.RESERVED);
//            file.setReservedBy(user);
//
//            // تسجيل العملية في FileLog
//            saveFileLog(file, user, "Check-Out");
//
//            return fileRepository.save(file);
//        }
//
//        // إرجاع ملف (Check-In)
//        public File checkInFile(UUID fileId, UUID userId, byte[] updatedContent) throws IllegalAccessException {
//            File file = fileRepository.findById(fileId)
//                    .orElseThrow(() -> new IllegalArgumentException("File not found"));
//
//            if (file.getReservationStatus() == FileStatus.FREE) {
//                throw new IllegalStateException("File is not reserved");
//            }
//
//            if (!file.getReservedBy().getId().equals(userId)) {
//                throw new IllegalAccessException("You are not authorized to check in this file");
//            }
//
//            file.setContent(updatedContent);
//            file.setReservationStatus(FileStatus.FREE);
//            file.setReservedBy(null);
//
//            // تسجيل العملية في FileLog
//            saveFileLog(file, userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found")), "Check-In");
//
//            return fileRepository.save(file);
//        }
//
//        private void saveFileLog(File file, User user, String action) {
//            FileLog log = FileLog.builder()
//                    .file(file)
//                    .user(user)
//                    .action(action)
//                    .build();
//
//            fileLogRepository.save(log);
//        }

    @Transactional
    public List<File> checkInFiles(List<UUID> fileIds, UUID userId, List<File> modifiedFiles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<File> files = fileRepository.findAllById(fileIds);

        // Validate: Ensure all files are free (not checked out)
        for (File file : files) {
            if (!FileStatus.FREE.equals(file.getReservationStatus())) {
                throw new IllegalStateException("One or more files are not Free.");
            }
        }

        // Check in all files (reserve them for the user)
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            File modifiedFile = modifiedFiles.get(i); // Get the modified file content

            // Reserve the file for the user
            file.setReservationStatus(FileStatus.RESERVED);
            file.setReservedBy(user); // Mark the user as the one who reserved the file

            // Replace the file content with the modified content
            file.setContent(modifiedFile.getContent()); // Update content to the modified file

            // Save the updated file state
            fileRepository.save(file);

            // Log the action (Check-In)
            FileLog log = FileLog.builder()
                    .file(file)
                    .user(user)
                    .action("Check-In")
                    .build();
            fileLogRepository.save(log);

            // Log the user (Check-In)
            UserLog ulog = UserLog.builder()
                    .user(user)
                    .action("Check-In")
                    .group(file.getGroup())
                    .build();
            userLogRepository.save(ulog);
        }

        return files;
    }
    @Transactional
    public List<File> checkOutFiles(List<UUID> fileIds, UUID userId, List<File> modifiedFiles) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<File> files = fileRepository.findAllById(fileIds);

        // Validate: Ensure each file is reserved and checked out by the user
        for (File file : files) {
            if (!FileStatus.RESERVED.equals(file.getReservationStatus()) || !file.getReservedBy().getId().equals(userId)) {
                throw new IllegalStateException("File is either not reserved by the user or already checked out.");
            }
        }

        // Check out all files (replace with modified content and set status to free)
        for (int i = 0; i < files.size(); i++) {
            File file = files.get(i);
            File modifiedFile = modifiedFiles.get(i); // Get the modified file content

            if (modifiedFile.getContent() != null) {
                // Ensure the content is updated properly, could be a path or byte array
                file.setContent(modifiedFile.getContent()); // Update content to the modified file
            } else {
                throw new IllegalArgumentException("Modified file content is missing.");
            }

            // Change file status back to FREE (making it available for others)
            file.setReservationStatus(FileStatus.FREE);
            file.setReservedBy(null); // Remove the user reservation

            // Save the updated file (Optimistic Locking will automatically handle version conflicts)
            fileRepository.save(file);

            // Log the action (Check-Out)
            FileLog log = FileLog.builder()
                    .file(file)
                    .user(user)
                    .action("Check-Out")
                    .build();
            fileLogRepository.save(log);

            // Log the action (Check-Out)
            UserLog ulog = UserLog.builder()
                    .user(user)
                    .group(file.getGroup())
                    .action("Check-Out")
                    .build();
            userLogRepository.save(ulog);
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
