package event.notificator.kafka;

public record Registration(
        Long id,
        Long userId,
        Long eventId
) {
}
