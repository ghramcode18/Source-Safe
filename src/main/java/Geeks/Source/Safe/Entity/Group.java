package Geeks.Source.Safe.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Table(name = "groups")  // Avoids using SQL reserved keywords
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    @ManyToMany
    @JoinTable(
            name = "group_user",
            joinColumns = @JoinColumn(name = "group_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    @JsonIgnore
    private Set<User> members;

    @OneToMany(mappedBy = "group")
    private Set<File> files;
}
