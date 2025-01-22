package Geeks.Source.Safe.controller;

import Geeks.Source.Safe.security.JwtUtil;
import Geeks.Source.Safe.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.UUID;

@Controller
public class ApiTesterController {
    @Autowired
    JwtUtil jwtUtil;
    @Autowired
    private FileService fileService;

    // Endpoint to handle the check-in process
    @GetMapping("/check-in")
    public String checkInFile(@RequestParam("fileId") UUID fileId,
                              @RequestParam("token") String token,
                              Model model) throws IOException {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        String username = jwtUtil.extractUsername(token);
        // Call the service to check-in the file
        fileService.checkInFileAndRedirect(fileId,username, token);

        // Set the fileId and token in the model for the download API link
        model.addAttribute("fileId", fileId);
        model.addAttribute("token", token);

        // Return the same view with a link to download the file
        return "api-checker";  // Will go back to the Thymeleaf template (api-checker.html)
    }
}

