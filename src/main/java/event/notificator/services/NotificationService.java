package event.notificator.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import event.notificator.NotificationRepository;
import event.notificator.entity.NotificationEntity;
import event.notificator.kafka.EventChangeMessage;
import event.notificator.kafka.EventFieldChange;
import event.notificator.mapper.NotificationMapper;
import event.notificator.model.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ObjectMapper objectMapper;

    public NotificationService(NotificationRepository notificationRepository, ObjectMapper objectMapper, NotificationMapper notificationMapper, ObjectMapper objectMapper1) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.objectMapper = objectMapper1;
    }

    public void processEventChange(EventChangeMessage message) {
        try {
            logger.info("Processing event change for event ID: {}", message.getEventId());

            Map<String, Object> changes = extractChangedFields(message);
            String changesJson = objectMapper.writeValueAsString(changes);
            String notificationMessage = createNotificationMessage(message);

            for (Long userId : message.getUsers()) {
                NotificationEntity notification = new NotificationEntity();
                notification.setUserId(userId);
                notification.setEventId(message.getEventId());
                notification.setChangedFieldsJson(changesJson);
                notification.setMessage(notificationMessage);
                notification.setCreatedAt(LocalDateTime.now());
                notification.setIsRead(false);

                notificationRepository.save(notification);
                logger.info("Saved notification with auto-generated ID: {}", notification.getId());
            }
        } catch (Exception e) {
            logger.error("Error processing event change: {}", e.getMessage(), e);
        }
    }

    public List<Notification> getUnreadNotifications(Long userId) {
        List<NotificationEntity> entities =
                notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        return notificationMapper.toDomainList(entities);
    }

    public void markAsRead(List<Long> notificationIds, Long userId) {
        notificationRepository.markAsReadByIdsAndUserId(notificationIds, userId);
    }

    public void cleanupOldNotifications() {
        notificationRepository.deleteNotificationsOlderThan7Days();
    }

    private String createNotificationMessage(EventChangeMessage message) {
        StringBuilder sb = new StringBuilder();

        sb.append("Мероприятие #").append(message.getEventId()).append(" изменено");

        if (message.getChangedById() != null) {
            sb.append(" пользователем #").append(message.getChangedById());
        } else {
            sb.append(" системой");
        }

        boolean hasChanges = false;

        if (message.getName() != null && message.getName().hasChanged()) {
            sb.append("Название: '").append(message.getName().getOldField())
                    .append("' на '").append(message.getName().getNewField());
            hasChanges = true;
        }

        if (message.getMaxPlaces() != null && message.getMaxPlaces().hasChanged()) {
            sb.append("Мест: ").append(message.getMaxPlaces().getOldField())
                    .append(" на ").append(message.getMaxPlaces().getNewField()).append(". ");
            hasChanges = true;
        }

        if (message.getDate() != null && message.getDate().hasChanged()) {
            sb.append("Дата: ").append(formatDateTime(message.getDate().getOldField()))
                    .append(" на ").append(formatDateTime(message.getDate().getNewField())).append(". ");
            hasChanges = true;
        }

        if (message.getCost() != null && message.getCost().hasChanged()) {
            sb.append("Стоимость: ").append(message.getCost().getOldField())
                    .append(" на ").append(message.getCost().getNewField()).append(" руб. ");
            hasChanges = true;
        }

        if (message.getDuration() != null && message.getDuration().hasChanged()) {
            sb.append("Длительность: ").append(message.getDuration().getOldField())
                    .append(" на ").append(message.getDuration().getNewField()).append(" мин. ");
            hasChanges = true;
        }

        if (message.getLocationId() != null && message.getLocationId().hasChanged()) {
            sb.append("Локация: #").append(message.getLocationId().getOldField())
                    .append(" на #").append(message.getLocationId().getNewField()).append(". ");
            hasChanges = true;
        }

        if (message.getStatus() != null && message.getStatus().hasChanged()) {
            sb.append("Статус: ").append(message.getStatus().getOldField())
                    .append(" на ").append(message.getStatus().getNewField()).append(". ");
            hasChanges = true;
        }

        if (!hasChanges) {
            sb.append("Детали изменений не указаны.");
        }

        return sb.toString();
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.toString().replace("T", " ") : "не указано";
    }

    private Map<String, Object> extractChangedFields(EventChangeMessage message) {
        Map<String, Object> changes = new HashMap<>();

        extractFieldChange(changes, "name", message.getName());
        extractFieldChange(changes, "maxPlaces", message.getMaxPlaces());
        extractFieldChange(changes, "date", message.getDate());
        extractFieldChange(changes, "cost", message.getCost());
        extractFieldChange(changes, "duration", message.getDuration());
        extractFieldChange(changes, "locationId", message.getLocationId());
        extractFieldChange(changes, "status", message.getStatus());

        changes.put("eventId", message.getEventId());
        if (message.getChangedById() != null) {
            changes.put("changedByUserId", message.getChangedById());
        }
        changes.put("changeTime", LocalDateTime.now().toString());

        return changes;
    }

    private <T> void extractFieldChange(Map<String, Object> changes, String fieldName, EventFieldChange<T> fieldChange) {
        if (fieldChange != null && fieldChange.hasChanged()) {
            Map<String, Object> fieldChangeMap = new HashMap<>();
            fieldChangeMap.put("oldValue", fieldChange.getOldField());
            fieldChangeMap.put("newValue", fieldChange.getNewField());
            fieldChangeMap.put("fieldType", getFieldType(fieldChange.getOldField()));

            changes.put(fieldName, fieldChangeMap);
        }
    }

    private String getFieldType(Object value) {
        if (value == null) return "unknown";

        return switch (value.getClass().getSimpleName()) {
            case "String" -> "string";
            case "Integer", "Long" -> "number";
            case "BigDecimal" -> "decimal";
            case "LocalDateTime" -> "datetime";
            default -> "object";
        };
    }
}
