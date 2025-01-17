package Geeks.Source.Safe.controller;

import Geeks.Source.Safe.DTO.AuthRequest;
import Geeks.Source.Safe.DTO.TokenResponse;
import Geeks.Source.Safe.Entity.User;
import Geeks.Source.Safe.security.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Register a new user.
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        return ResponseEntity.ok(authService.register(user));
    }

    /**
     * Log in a user and return both access and refresh tokens.
     */
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@RequestBody AuthRequest request) {
        System.out.println(request.toString());
        TokenResponse tokenResponse = authService.login(request);
        return ResponseEntity.ok(tokenResponse);
    }

    /**
     * Refresh access token using a valid refresh token.
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<String> refreshAccessToken(@RequestParam String refreshToken) {
        String newAccessToken = authService.refreshAccessToken(refreshToken);
        return ResponseEntity.ok(newAccessToken);
    }
}
