package Geeks.Source.Safe.controller;

import Geeks.Source.Safe.Entity.UserNotification;
import Geeks.Source.Safe.Entity.User;
import Geeks.Source.Safe.repo.UserRepository;
import Geeks.Source.Safe.repo.UserNotificationRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.util.HtmlUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@Controller
public class NotificationController {

    private final UserRepository userRepository;
    private final UserNotificationRepository userNotificationRepository;

    @Autowired
    public NotificationController(UserRepository userRepository, UserNotificationRepository userNotificationRepository) {
        this.userRepository = userRepository;
        this.userNotificationRepository = userNotificationRepository;
    }

    @MessageMapping("/notification/{userId}")
    @SendTo("/topic/notifications/{userId}")
    public UserNotification sendNotification(@PathVariable UUID userId, String notificationMessage) throws InterruptedException {
        // Retrieve user based on the userId
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        // Create a UserNotification
        UserNotification userNotification = new UserNotification();
        userNotification.setUser(user);
        userNotification.setMessage("Hello, " + HtmlUtils.htmlEscape(notificationMessage));

        // Save the notification in the database
        UserNotification savedNotification = userNotificationRepository.save(userNotification);

        return savedNotification;
    }
}