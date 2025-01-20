package Geeks.Source.Safe.Entity;

import Geeks.Source.Safe.Entity.Enum.FileStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Table(name = "file")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "file_name", nullable = false)
    private String fileName;


    @Column(name = "file_path", nullable = false)
    private String filePath; // The path to the file on the server

    @ManyToOne
    @JoinColumn(name = "group_id")
    @JsonIgnore
    private Group group;

    @OneToMany(mappedBy = "file")
    private Set<FileLog> fileLogs;

    @Enumerated(EnumType.STRING)
    private FileStatus reservationStatus = FileStatus.FREE;

    @ManyToOne
    @JoinColumn(name = "reserved_by")
    private User reservedBy;

    @OneToMany(mappedBy = "textFile")
    @JsonIgnore
    private List<ReservationHistory> reservationHistories;

    @Version
    private int version;

}
