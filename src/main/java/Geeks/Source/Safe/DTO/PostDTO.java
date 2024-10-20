package havebreak.SocialSphere.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import havebreak.SocialSphere.Entity.CommentEntity;
import havebreak.SocialSphere.Entity.Enum.PostEnum;
import havebreak.SocialSphere.Entity.LikeEntity;
import havebreak.SocialSphere.Entity.UserEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostDTO {

    private String id;

    private String title;

    private PostEnum type;

    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    private UserDTO user;

    private Long likeCount;

    private List<LikeDTO> likes;

    private List<CommentDTO> comments;
}
