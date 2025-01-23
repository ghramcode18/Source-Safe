package Geeks.Source.Safe.Entity;


import Geeks.Source.Safe.Entity.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Table(name = "user_log")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
public class UserLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private Group group;

    @Column(nullable = false)
    private String action; // "Check-In", "Check-Out", etc.

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;
}
