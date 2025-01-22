package Geeks.Source.Safe.controller;

import Geeks.Source.Safe.Entity.Enum.Role;
import Geeks.Source.Safe.Entity.User;
import Geeks.Source.Safe.repo.UserRepository;
import Geeks.Source.Safe.security.JwtUtil;
import Geeks.Source.Safe.service.ExportService;
import Geeks.Source.Safe.service.FileService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/export")
public class AdminExportController {

    private final UserRepository userRepository;
    private final ExportService exportService;

    @Autowired
    private JwtUtil jwtUtil;

    public AdminExportController(UserRepository userRepository, ExportService exportService) {
        this.userRepository = userRepository;
        this.exportService = exportService;
    }

    @GetMapping("/table")
    public ResponseEntity<ByteArrayResource> exportTable(
            @RequestParam String format,
            @RequestHeader("Authorization") String token) throws IOException {


        String username = jwtUtil.extractUsername(token.substring(7));
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!user.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException("Only admin can access this endpoint.");
        }

        // Export table
        ByteArrayResource resource;
        String fileName;
        MediaType contentType;

        if ("csv".equalsIgnoreCase(format)) {
            resource = new ByteArrayResource(exportService.exportTableAsCSV());
            fileName = "table_export.csv";
            contentType = MediaType.TEXT_PLAIN;
        } else if ("pdf".equalsIgnoreCase(format)) {
            resource = new ByteArrayResource(exportService.exportTableAsPDF());
            fileName = "table_export.pdf";
            contentType = MediaType.APPLICATION_PDF;
        } else {
            throw new IllegalArgumentException("Invalid format. Use 'csv' or 'pdf'.");
        }

        // Return file as a response
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(contentType)
                .body(resource);
    }
}
