package Geeks.Source.Safe.DTO;

import lombok.*;

import java.util.UUID;

@Setter
@Getter
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String fullName;
    private String userName;
    private String email;
    private String password;
    private String phoneNumber;
    private boolean isActive;
    private boolean verified;
}
