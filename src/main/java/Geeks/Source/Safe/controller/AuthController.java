package havebreak.SocialSphere.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import havebreak.SocialSphere.DTO.LoginRequestDTO;
import havebreak.SocialSphere.DTO.UserDTO;
import havebreak.SocialSphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO user) throws JsonProcessingException {
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequest) throws JsonProcessingException {
        return userService.loginUser(loginRequest);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@RequestBody UserDTO userDTO) throws JsonProcessingException {
        return userService.logoutUser(userDTO);
    }
}
