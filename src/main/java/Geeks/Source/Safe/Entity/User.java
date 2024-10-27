package Geeks.Source.Safe.Entity;

import Geeks.Source.Safe.Entity.Enum.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Table(name = "users")  // Avoids using SQL reserved keywords
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String userName;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    @Email(regexp = ".+[@].+[\\.].+")
    private String email;

    @ManyToMany(mappedBy = "members")
    @JsonIgnore
    private Set<Group> groups;

    @Enumerated(EnumType.STRING)
    private Role role;
}
