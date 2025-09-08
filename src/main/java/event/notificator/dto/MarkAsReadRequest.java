package event.notificator.dto;

import java.util.List;

public record MarkAsReadRequest (
        List<Long> notificationIds
) {
}
