package Geeks.Source.Safe.Entity;

import Geeks.Source.Safe.Entity.Enum.CheckOutStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "reservation_history")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ReservationHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "text_file_id")
    private File textFile;

    @ManyToOne
    @JoinColumn(name = "reserved_user_id")
    @JsonIgnore
    private User user;

    @Enumerated(EnumType.STRING)
    private CheckOutStatus checkOutStatus;

    private LocalDateTime timestamp;

    @PrePersist
    private void setTimestamp() {
        this.timestamp = LocalDateTime.now();
        this.expirationTime = LocalDateTime.now().plusMinutes(1L);
    }

    private LocalDateTime expirationTime;

    private LocalDateTime checkOutEndTime;

}
