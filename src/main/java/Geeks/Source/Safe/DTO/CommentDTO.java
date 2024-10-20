package havebreak.SocialSphere.DTO;
import lombok.*;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {

    private String id;
    private String content;
    private Date createdTimestamp;
    private String postId;
    private String  user;
}