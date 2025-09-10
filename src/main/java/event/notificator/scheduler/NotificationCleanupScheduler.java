package event.notificator.scheduler;

import event.notificator.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class NotificationCleanupScheduler {
    private static final Logger logger = LoggerFactory.getLogger(NotificationCleanupScheduler.class);
    private final NotificationService notificationService;

    public NotificationCleanupScheduler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Scheduled(cron = "${notification.cleanup.cron:0 0 0 * * *}")
    public void cleanupOldNotifications() {
        logger.info("Starting notification cleanup...");
        notificationService.cleanupOldNotifications();
        logger.info("Notification cleanup completed");
    }
}
