package havebreak.SocialSphere.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.util.Date;

@Entity
@Table(name = "comments")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String content;

    @Column(name = "created_timestamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTimestamp = new Date();

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;

}
