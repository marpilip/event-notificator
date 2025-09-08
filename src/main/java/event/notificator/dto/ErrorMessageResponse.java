package event.notificator.dto;

import java.time.LocalDateTime;

public record ErrorMessageResponse(String message, LocalDateTime timestamp) {
}
