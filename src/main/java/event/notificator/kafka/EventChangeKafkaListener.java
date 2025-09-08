package event.notificator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import event.notificator.services.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventChangeKafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(EventChangeKafkaListener.class);
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;

    public EventChangeKafkaListener(NotificationService notificationService, ObjectMapper objectMapper) {
        this.notificationService = notificationService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "events-changes")
    public void consumeEventChange(String jsonMessage) {
        logger.info("Received event change message: {}", jsonMessage);

        try {
            EventChangeMessage message = objectMapper.readValue(jsonMessage, EventChangeMessage.class);
            notificationService.processEventChange(message);
            logger.info("Successfully processed event change for eventId: {}", message.getEventId());
        } catch (Exception e) {
            logger.error("Error processing event change message: {}", e.getMessage(), e);
        }
    }
}
