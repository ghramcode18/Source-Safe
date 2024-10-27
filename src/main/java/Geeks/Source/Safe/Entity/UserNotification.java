package Geeks.Source.Safe.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;


@Builder
@Entity
@Table(name = "user_notification")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private boolean isRead;
}
