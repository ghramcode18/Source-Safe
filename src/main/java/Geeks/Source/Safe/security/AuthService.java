package Geeks.Source.Safe.security;

import Geeks.Source.Safe.DTO.AuthRequest;
import Geeks.Source.Safe.DTO.TokenResponse;
import Geeks.Source.Safe.Entity.User;
import Geeks.Source.Safe.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private  JwtUtil jwtUtil;  // Utility class for handling JWT creation and validation

    @Autowired
    private  UserRepository userRepository;  // Repository to interact with User data

    // Constructor-based injection for required dependencies

    @Autowired

    private PasswordEncoder passwordEncoder; // Add PasswordEncoder

    // Register a new user
    public String register(User user) {
        // Check if the username already exists
        if (userRepository.existsByUserName(user.getUserName()) ||userRepository.existsByEmail(user.getEmail())) {
            return "Username or Email is already taken";
        }
        // Encrypt the user's password before saving it
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // Save the user in the repository
        userRepository.save(user);
        return "User registered successfully";
    }

    // Login method for authenticating users and generating tokens
    public TokenResponse login(AuthRequest request) {
        Optional<User> userOptional = userRepository.findByUserName(request.getUserName());

        // Check if user exists and if password matches
        if (userOptional.isEmpty() ||  userOptional.get().getPassword()==null) {
            throw new RuntimeException("Invalid credentials");  // Ideally, you should use a more specific exception like `BadCredentialsException`
        }

        // If credentials are valid, generate JWT tokens
        User user = userOptional.get();
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);
        return new TokenResponse(accessToken, refreshToken);
    }

    // Refresh the access token using a valid refresh token
    public String refreshAccessToken(String refreshToken) {
        if (jwtUtil.isTokenValid(refreshToken, null)) {
            String username = jwtUtil.extractUsername(refreshToken);
            Optional<User> userOptional = userRepository.findByUserName(username);

            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return jwtUtil.generateAccessToken(user);  // Generate a new access token for the user
            }
        }

        throw new RuntimeException("Invalid refresh token");  // Again, consider using a more specific exception here
    }
}
