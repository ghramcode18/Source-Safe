package Geeks.Source.Safe.Entity;

import Geeks.Source.Safe.Entity.Enum.RequestStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name = "file_requests")
public class FileRequest {
        @Id
        @GeneratedValue(strategy = GenerationType.UUID)
        private UUID id;

        @ManyToOne
        @JoinColumn(name = "group_id", nullable = false)
        private Group group;

        @ManyToOne
        @JoinColumn(name = "user_id", nullable = false)
        private User requester;

        @Column(nullable = false)
        private String fileName;

        @Lob
        private byte[] content;

        @Enumerated(EnumType.STRING)
        private RequestStatus status = RequestStatus.PENDING;
    }



