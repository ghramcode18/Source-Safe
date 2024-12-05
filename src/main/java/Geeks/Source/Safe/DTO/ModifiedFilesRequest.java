package Geeks.Source.Safe.DTO;

import Geeks.Source.Safe.Entity.File;

import java.util.List;


import lombok.*;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ModifiedFilesRequest {
    private List<File> modifiedFiles;

    // Getter and Setter
    public List<File> getModifiedFiles() {
        return modifiedFiles;
    }

    public void setModifiedFiles(List<File> modifiedFiles) {
        this.modifiedFiles = modifiedFiles;
    }
}
