package event.notificator.mapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import event.notificator.dto.NotificationDto;
import event.notificator.entity.NotificationEntity;
import event.notificator.model.Notification;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class NotificationMapper {
    private final ObjectMapper objectMapper;

    public NotificationMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public NotificationDto toDto(NotificationEntity entity) {
        try {
            Map<String, Object> changes = objectMapper.readValue(
                    entity.getChangedFieldsJson(),
                    new TypeReference<>() {}
            );

            return new NotificationDto(
                    entity.getId(),
                    entity.getEventId(),
                    changes,
                    entity.getMessage(),
                    entity.getCreatedAt(),
                    entity.getIsRead()
            );
        } catch (Exception e) {
            return new NotificationDto(
                    entity.getId(),
                    entity.getEventId(),
                    Map.of(),
                    entity.getMessage(),
                    entity.getCreatedAt(),
                    entity.getIsRead()
            );
        }
    }

    public Notification toDomain(NotificationEntity entity) {
        try {
            Map<String, Object> changes = objectMapper.readValue(
                    entity.getChangedFieldsJson(),
                    new TypeReference<>() {
                    }
            );

            return new Notification(
                    entity.getId(),
                    entity.getEventId(),
                    changes,
                    entity.getMessage(),
                    entity.getCreatedAt(),
                    entity.getIsRead()
            );
        } catch (Exception e) {
            return new Notification(
                    entity.getId(),
                    entity.getEventId(),
                    Map.of(),
                    entity.getMessage(),
                    entity.getCreatedAt(),
                    entity.getIsRead()
            );
        }
    }

    public NotificationEntity toEntity(Notification domain) {
        NotificationEntity entity = new NotificationEntity();
        entity.setId(domain.getId());
        entity.setEventId(domain.getEventId());
        entity.setMessage(domain.getMessage());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setIsRead(domain.getIsRead());

        try {
            String changesJson = objectMapper.writeValueAsString(domain.getChangedFields());
            entity.setChangedFieldsJson(changesJson);
        } catch (Exception e) {
            entity.setChangedFieldsJson("{}");
        }

        return entity;
    }

    public List<Notification> toDomainList(List<NotificationEntity> entities) {
        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    public NotificationDto toDto(Notification domain) {
        return new NotificationDto(
                domain.getId(),
                domain.getEventId(),
                domain.getChangedFields(),
                domain.getMessage(),
                domain.getCreatedAt(),
                domain.getIsRead()
        );
    }
}
