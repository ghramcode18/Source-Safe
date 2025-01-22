    package Geeks.Source.Safe.Entity;

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
    @Table(name = "file_log")
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @Data
    @EntityListeners(AuditingEntityListener.class)
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
        private String action;

        @CreatedDate
        @Temporal(TemporalType.TIMESTAMP)
        private LocalDateTime createdAt;

        @LastModifiedDate
        @Temporal(TemporalType.TIMESTAMP)
        private LocalDateTime updatedAt;

        // Constructors, getters, and setters
        public FileLog(File file, User user, String actionType) {
            this.file = file;
            this.user = user;
            this.action = actionType;
        }
    }
