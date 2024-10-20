package havebreak.SocialSphere.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "likes")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Setter
@Getter
public class LikeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID UUID;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private PostEntity post;
}
