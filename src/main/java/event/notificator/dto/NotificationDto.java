package event.notificator.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.Map;

public record NotificationDto(
        Long id,
        Long eventId,
        Map<String, Object> changedFields,
        String message,

        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime createdAt,
        Boolean isRead

) {
}
