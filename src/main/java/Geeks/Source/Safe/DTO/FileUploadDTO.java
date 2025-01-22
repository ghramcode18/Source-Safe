package Geeks.Source.Safe.DTO;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadDTO {
    public String fileName;
    public String description;
    public MultipartFile file;
}
