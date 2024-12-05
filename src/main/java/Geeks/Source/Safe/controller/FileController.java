package Geeks.Source.Safe.controller;

import Geeks.Source.Safe.DTO.ModifiedFilesRequest;
import Geeks.Source.Safe.Entity.File;
import Geeks.Source.Safe.service.FileService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

//    /**
//     * Check out a file (reserve for editing)
//     *
//     * @param fileId UUID of the file to check out
//     * @param userId UUID of the user reserving the file
//     * @return The updated file
//     */
//    @PostMapping("/{fileId}/check-out")
//    public ResponseEntity<List<File>> checkOutFile(@PathVariable  List<UUID> fileIds, @RequestParam UUID userId,@RequestParam List<byte[]> modifiedContents) {
//        List<File> file = fileService.checkOutFiles(fileIds, userId,modifiedContents);
//        return ResponseEntity.ok(file);
//    }

//    /**
//     * Check in a file (return after editing)
//     *
//     * @param fileId UUID of the file to check in
//     * @param userId UUID of the user returning the file
//     * @return The updated file
//     */
//    @PostMapping("/{fileId}/check-in")
//    public ResponseEntity<File> checkInFile(
//            @PathVariable UUID fileId,
//            @RequestParam UUID userId,
//            @RequestParam("file") MultipartFile updatedFile) {
//        try {
//            byte[] updatedContent = updatedFile.getBytes();
//            File file = fileService.checkInFile(fileId, userId, updatedContent);
//            return ResponseEntity.ok(file);
//        } catch (IOException e) {
//            return ResponseEntity.badRequest().body(null);
//        } catch (IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }


    /**
     * Endpoint for checking in multiple files.
     * Files are reserved by the user and content is updated.
     *
     * @param userId       The ID of the user checking in the files.
     * @param fileIds      List of file IDs to check in.
     * @return ResponseEntity containing the checked-in files.
     */
    @PostMapping("/check-in")
    public ResponseEntity<List<File>> checkInFiles(
            @RequestParam UUID userId,
            @RequestParam List<UUID> fileIds,
            @Valid @RequestBody ModifiedFilesRequest request) { // New DTO class

        try {
            List<File> checkedInFiles = fileService.checkInFiles(fileIds, userId, request.getModifiedFiles());
            return ResponseEntity.ok(checkedInFiles);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }


    /**
     * Endpoint for checking out multiple files.
     * Modified files are returned and the status is set to FREE.
     *
     * @param userId       The ID of the user checking out the files.
     * @param fileIds      List of file IDs to check out.
     * @return ResponseEntity containing the checked-out files.
     */
    @PostMapping("/check-out")
    public ResponseEntity<List<File>> checkOutFiles(
            @RequestParam UUID userId,
            @RequestParam List<UUID> fileIds,
            @Valid @RequestBody ModifiedFilesRequest modifiedFiles){

        try {
            List<File> checkedOutFiles = fileService.checkOutFiles(fileIds, userId, modifiedFiles.getModifiedFiles());
            return ResponseEntity.ok(checkedOutFiles);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
