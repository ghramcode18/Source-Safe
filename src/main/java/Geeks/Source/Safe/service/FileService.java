package Geeks.Source.Safe.service;

import Geeks.Source.Safe.Entity.FileEntity;
import Geeks.Source.Safe.repo.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    @Autowired
    private FileRepository fileRepository;

    public void reserveFile(Long fileId, String username) {
        FileEntity file = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.isReserved()) {
            file.setReserved(true);
            file.setReservedBy(username);
            fileRepository.save(file);
        } else {
            throw new RuntimeException("File is already reserved by another user");
        }
    }

    public void releaseFile(Long fileId, String username) {
        FileEntity file = fileRepository.findById(fileId).orElseThrow(() -> new RuntimeException("File not found"));

        if (file.isReserved() && file.getReservedBy().equals(username)) {
            file.setReserved(false);
            file.setReservedBy(null);
            fileRepository.save(file);
        } else {
            throw new RuntimeException("File is not reserved by the current user");
        }
    }
}