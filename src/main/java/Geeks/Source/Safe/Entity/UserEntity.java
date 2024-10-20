package havebreak.SocialSphere.Entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import havebreak.SocialSphere.Entity.Enum.GenderEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Builder
@Entity
@Table(name = "user")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(name = "date_of_birth")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dob;

    private GenderEnum gender;

    @Column(unique = true)
    private String phoneNumber;

    @Column(unique = true,nullable = false)
    private String userName;

    @Column(unique = true,nullable = false)
    @Email(regexp = ".+[@].+[\\.].+")
    private String email;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    private boolean isActive;

    private boolean verified = false;  // Default to false


    @JsonIgnore
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<PostEntity> postEntities;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LikeEntity> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CommentEntity> comments;
}