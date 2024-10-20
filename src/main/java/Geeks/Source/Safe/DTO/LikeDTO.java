package havebreak.SocialSphere.DTO;
import havebreak.SocialSphere.Entity.PostEntity;
import havebreak.SocialSphere.Entity.UserEntity;
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
