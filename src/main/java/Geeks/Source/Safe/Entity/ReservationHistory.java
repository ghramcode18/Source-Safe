package Geeks.Source.Safe.Entity;

import Geeks.Source.Safe.Entity.Enum.CheckOutStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Entity
@Table(name = "reservation_history")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Data
@EntityListeners(AuditingEntityListener.class)
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

    @CreatedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime updatedAt;

    @PrePersist
    private void setTimestamp() {
        this.createdAt = LocalDateTime.now();
        this.expirationTime = LocalDateTime.now().plusMinutes(1L);
    }

    private LocalDateTime expirationTime;

    private LocalDateTime checkOutEndTime;

}
