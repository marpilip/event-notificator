package event.notificator.controller;

import event.notificator.dto.MarkAsReadRequest;
import event.notificator.dto.NotificationDto;
import event.notificator.mapper.NotificationMapper;
import event.notificator.model.Notification;
import event.notificator.services.NotificationService;
import event.notificator.model.User;
import event.notificator.services.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationController {
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);

    private final NotificationService notificationService;
    private final AuthenticationService authenticationService;
    private final NotificationMapper notificationMapper;

    public NotificationController(NotificationService notificationService,
                                  AuthenticationService authenticationService,
                                  NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.authenticationService = authenticationService;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping
    public ResponseEntity<List<NotificationDto>> getUnreadNotifications() {
        User currentUser = authenticationService.getCurrentAuthenticatedUserOrThrow();
        logger.info("Fetching notifications for user ID: {}", currentUser.id());

        List<Notification> notifications =
                notificationService.getUnreadNotifications(currentUser.id());

        logger.info("Found {} notifications", notifications.size());

        return ResponseEntity.status(HttpStatus.OK)
                .body(notifications.stream()
                        .map(notificationMapper::toDto)
                        .toList());
    }

    @PostMapping()
    public ResponseEntity<Void> markNotificationsAsRead(@RequestBody MarkAsReadRequest request) {
        User currentUser = authenticationService.getCurrentAuthenticatedUserOrThrow();
        notificationService.markAsRead(request.notificationIds(), currentUser.id());

        return ResponseEntity.noContent().build();
    }
}
