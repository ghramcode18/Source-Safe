package havebreak.SocialSphere.controller;

import havebreak.SocialSphere.DTO.CommentDTO;
import havebreak.SocialSphere.DTO.PostDTO;
import havebreak.SocialSphere.Entity.UserEntity;
import havebreak.SocialSphere.secuirity.JwtUtil;
import havebreak.SocialSphere.service.PostService;
import havebreak.SocialSphere.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;  // A service for handling user-related logic

    // Create a new post
    @PostMapping("/createPost")
    public ResponseEntity<?> createPost(@RequestBody PostDTO postDTO, @RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        try{
        String username = jwtUtil.extractUsername(token.substring(7));  // Remove 'Bearer ' prefix
        UserEntity user = userService.getUserByUsername(username);  // Fetch the user by username

        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized user.");
        }

        postService.createPost(postDTO, user);
        response.put("message", "Post created successfully.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
        response.put("message", "Post failed to create.");
        response.put("error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }}

    // View a feed of posts
    @GetMapping("/feed")
    public ResponseEntity<List<PostDTO>> getFeed() {
        List<PostDTO> feed = postService.getFeed();
        return ResponseEntity.ok(feed);
    }

    // Like a post
    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable String postId, @RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        try {
        String username = jwtUtil.extractUsername(token.substring(7));  // Remove 'Bearer ' prefix
        UserEntity user = userService.getUserByUsername(username);  // Fetch the user by username

        if (user == null) {
            return ResponseEntity.status(401).body("Unauthorized user.");
        }

        postService.likePost(postId, user);
        response.put("message", "Post liked successfully!");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    } catch (Exception e) {
        response.put("message", "some thing wrong please try again later.");
        response.put("error", e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }}

    @PostMapping("/{postId}/comment")
    public ResponseEntity<?> addComment(@PathVariable String postId,
                                        @RequestBody CommentDTO commentDTO,
                                        @RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();
        try {
            String username = jwtUtil.extractUsername(token.substring(7));  // Remove 'Bearer ' prefix
            UserEntity user = userService.getUserByUsername(username);  // Fetch the user by username

            // Log the retrieved user for debugging
            if (user == null) {
                return ResponseEntity.status(401).body("Unauthorized user.");
            }

            // Log the user ID and check if it's correctly fetched
            System.out.println("User ID: " + user.getId());

            postService.addComment(postId, commentDTO, user);

            response.put("message", "Comment added successfully!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            response.put("message", "Something went wrong, please try again later.");
            response.put("error", e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
