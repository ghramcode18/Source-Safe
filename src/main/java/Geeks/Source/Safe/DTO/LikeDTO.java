package Geeks.Source.Safe.DTO;
import Geeks.Source.Safe.Entity.PostEntity;
import Geeks.Source.Safe.Entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeDTO {

    private UUID UUID;
    private UserDTO user;
    private PostDTO post;
}
