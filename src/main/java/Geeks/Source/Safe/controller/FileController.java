package Geeks.Source.Safe.controller;


import Geeks.Source.Safe.Entity.File;
import Geeks.Source.Safe.service.FileService;

import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<List<File>> checkInFiles(
            @RequestParam List<UUID> fileIds,
            @RequestParam UUID userId,
            @RequestParam List<MultipartFile> modifiedFiles) throws IOException {

        List<File> updatedFiles = fileService.checkInFiles(fileIds, userId, modifiedFiles);
        return ResponseEntity.ok(updatedFiles);
    }

    @PostMapping("/check-out")
    public ResponseEntity<List<File>> checkOutFiles(
            @RequestParam List<UUID> fileIds,
            @RequestParam UUID userId,
            @RequestParam List<MultipartFile> uploadedFiles) throws IOException {

        List<File> checkedOutFiles = fileService.checkOutFiles(fileIds, userId, uploadedFiles);
        return ResponseEntity.ok(checkedOutFiles);
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<FileSystemResource> downloadFile(@PathVariable UUID fileId) {
        File file = fileService.getFileById(fileId);
        String filePath = file.getFilePath();
        FileSystemResource resource = new FileSystemResource(new java.io.File(filePath));

        if (!resource.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getFileName());
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }
}
