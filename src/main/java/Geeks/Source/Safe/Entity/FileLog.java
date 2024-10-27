package Geeks.Source.Safe.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "file_log")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class FileLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String action;  // "Check-In", "Check-Out", etc.

    @Column(nullable = false)
    private LocalDateTime timestamp;
}
