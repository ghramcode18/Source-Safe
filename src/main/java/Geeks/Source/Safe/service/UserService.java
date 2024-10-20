package havebreak.SocialSphere.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import havebreak.SocialSphere.DTO.JwtResponseDTO;
import havebreak.SocialSphere.DTO.LoginRequestDTO;
import havebreak.SocialSphere.DTO.UserDTO;
import havebreak.SocialSphere.Entity.UserEntity;
import havebreak.SocialSphere.repo.UserRepository;
import havebreak.SocialSphere.secuirity.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    public ResponseEntity<Object> registerUser(UserDTO user) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();

        try {

            Optional<UserEntity> existingUserByEmail = userRepository.findByEmail(user.getEmail());
            Optional<UserEntity> existingUserByUsername = userRepository.findByUserName(user.getUserName());

            if (existingUserByEmail.isPresent() || existingUserByUsername.isPresent()) {
                response.put("error", "User with this email or username already exists");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }


            user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));

            // Save the new user
            UserEntity newUser = new UserEntity();
            newUser.setFullName(user.getFullName());
            newUser.setUserName(user.getUserName());
            newUser.setEmail(user.getEmail());
            newUser.setPassword(user.getPassword()); // encrypted
            newUser.setPhoneNumber(user.getPhoneNumber());
            newUser.setActive(true); // Activate the account

            userRepository.save(newUser);

            response.put("message", "User registered successfully.");
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        } catch (Exception e) {
            response.put("message", "Registration failed.");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Object> loginUser(LoginRequestDTO loginRequest) throws JsonProcessingException {
        try {
            Optional<UserEntity> userOpt = userRepository.findByEmail(loginRequest.getEmail());

            if (userOpt.isPresent()) {
                UserEntity user = userOpt.get();

                // Check password match
                if (new BCryptPasswordEncoder().matches(loginRequest.getPassword(), user.getPassword())) {
                    // Generate JWT token with an updated expiration
                    String token = jwtUtil.generateToken(user.getUserName());

                    // Mark user as active
                    user.setActive(true);
                    userRepository.save(user);

                    // Return the JWT token
                    return ResponseEntity.ok(new JwtResponseDTO(token));
                }
            }

            return ResponseEntity.status(401).body("Invalid credentials");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Login failed: " + e.getMessage());
        }
    }


//    public ResponseEntity<Object> loginUser(LoginRequestDTO loginRequest) throws JsonProcessingException {
//        try {
//            Optional<UserEntity> userOpt = userRepository.findByEmail(loginRequest.getEmail());
//            if (userOpt.isPresent()) {
//                UserEntity user = userOpt.get();
//
//                if (new BCryptPasswordEncoder().matches(loginRequest.getPassword(), user.getPassword())) {
//                    // Generate JWT token
//                    String token = jwtUtil.generateToken(user.getUserName());
//                    UserEntity activeUser = new UserEntity();
//                    userOpt.get().setActive(true);
//                    userRepository.save(userOpt.get());
//                    return ResponseEntity.ok(new JwtResponseDTO(token));
//
//                }
//            }
//            return ResponseEntity.status(401).body("Invalid credentials");
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("Login failed: " + e.getMessage());
//        }
//    }

    // Logout (you might want to implement token invalidation here)
    public ResponseEntity<?> logoutUser(UserDTO userDTO) throws JsonProcessingException {
        Map<String, String> response = new HashMap<>();

        try {
            UserEntity user = userRepository.findByEmail(userDTO.getEmail()).get();
            user.setActive(false);
            userRepository.save(user);
            response.put("message", "User logged out successfully.");
            return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            response.put("message", "Something went wrong, please try again.");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUserName(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
