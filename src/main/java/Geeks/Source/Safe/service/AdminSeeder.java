package Geeks.Source.Safe.service;



import Geeks.Source.Safe.Entity.Enum.Role;
import Geeks.Source.Safe.Entity.User;
import Geeks.Source.Safe.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;



@Component
public class AdminSeeder {

    private final UserRepository userRepository;

    public AdminSeeder(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void seedAdminUser() {
        // Check if the admin user already exists
        if (userRepository.findByUserName("admin").isEmpty()) {
            // Create a new admin user
            User adminUser = User.builder()
                    .userName("admin")
                    .email("admin@example.com")
                    .password("admin123")
                    .fullName("admin")
                    .role(Role.ADMIN)
                    .build();

            // Save the admin user to the database
            userRepository.save(adminUser);
            System.out.println("Admin user created: username=admin, password=admin123");
        } else {
            System.out.println("Admin user already exists.");
        }
    }
}
