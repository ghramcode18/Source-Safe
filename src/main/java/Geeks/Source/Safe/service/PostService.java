package havebreak.SocialSphere.service;

import havebreak.SocialSphere.DTO.CommentDTO;
import havebreak.SocialSphere.DTO.PostDTO;
import havebreak.SocialSphere.DTO.UserDTO;
import havebreak.SocialSphere.Entity.*;
import havebreak.SocialSphere.repo.CommentRepository;
import havebreak.SocialSphere.repo.LikeRepository;
import havebreak.SocialSphere.repo.PostRepository;
import havebreak.SocialSphere.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    // Create a new post
    public void createPost(PostDTO postDTO, UserEntity user) {
        PostEntity postEntity = new PostEntity();
        postEntity.setTitle(postDTO.getTitle());
        postEntity.setType(postDTO.getType());
        postEntity.setContent(postDTO.getContent());
        postEntity.setUser(user);
        postEntity.setCreatedAt(LocalDateTime.now());
        postEntity.setLikeCount(0L);
        postRepository.save(postEntity);
    }

    // View a feed of posts
    public List<PostDTO> getFeed() {
        List<PostEntity> posts = postRepository.findAll();
        return posts.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    private PostDTO convertToDTO(PostEntity post) {
        PostDTO dto = new PostDTO();
        dto.setId(post.getId().toString());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setType(post.getType());
        dto.setCreatedAt(post.getCreatedAt());

        // Convert UserEntity to UserDTO
        UserDTO userDTO = new UserDTO();
        userDTO.setId(UUID.fromString(post.getUser().getId().toString()));
        userDTO.setFullName(post.getUser().getFullName());
        userDTO.setUserName(post.getUser().getUserName());
        userDTO.setEmail(post.getUser().getEmail());
        userDTO.setPassword(post.getUser().getPassword());
        userDTO.setPhoneNumber(post.getUser().getPhoneNumber());
        userDTO.setVerified(post.getUser().isVerified());
        userDTO.setActive(post.getUser().isActive());

        // Add userDTO to the postDTO
        dto.setUser(userDTO);
        dto.setLikeCount(post.getLikeCount());

        // Convert List<CommentEntity> to List<CommentDTO>
        List<CommentDTO> commentDTOs = post.getComments().stream()
                .map(this::convertCommentToDTO) // Call the conversion method for each comment
                .collect(Collectors.toList());
        dto.setComments(commentDTOs); // Set the list of CommentDTOs

        // dto.setImages(post.getImage()); // Add image if necessary

        return dto;
    }

    // Method to convert CommentEntity to CommentDTO
    private CommentDTO convertCommentToDTO(CommentEntity comment) {
        CommentDTO dto = new CommentDTO();
        dto.setId(comment.getId().toString());
        dto.setContent(comment.getContent());
        dto.setPostId(comment.getPost().getId().toString()); // Assuming you have a getPost() method
        dto.setUser(comment.getUser().getId().toString());
        dto.setCreatedTimestamp(comment.getCreatedTimestamp());

        return dto;
    }



    // Like a post
    public void likePost(String  postId, UserEntity user) {
        PostEntity post = postRepository.findById(UUID.fromString(postId)).orElseThrow(() -> new RuntimeException("Post not found"));

        LikeEntity like = new LikeEntity();
        like.setPost(post);
        like.setUser(user);
        likeRepository.save(like);

        post.setLikeCount(post.getLikeCount() + 1);
        postRepository.save(post);
    }

    public void addComment(String postId, CommentDTO commentDTO, UserEntity user) {
        // Fetch the post; if not found, an exception is thrown
        PostEntity post = postRepository.findById(UUID.fromString(postId))
                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Create a new comment entity
        CommentEntity comment = new CommentEntity();
        comment.setContent(commentDTO.getContent());
        comment.setPost(post); // Ensure post is correctly set
        comment.setUser(userRepository.findById(user.getId()).get()); // Ensure user is not null and properly populated
        comment.setCreatedTimestamp(new Date());
        // Save the comment
        commentRepository.save(comment);
    }




}

