package Geeks.Source.Safe.controller;

import Geeks.Source.Safe.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileService fileService;

    @PutMapping("/{fileId}/reserve")
    public void reserveFile(@PathVariable Long fileId, @RequestParam String username) {
        fileService.reserveFile(fileId, username);
    }

    @PutMapping("/{fileId}/release")
    public void releaseFile(@PathVariable Long fileId, @RequestParam String username) {
        fileService.releaseFile(fileId, username);
    }
}