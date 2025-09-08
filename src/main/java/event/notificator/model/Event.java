package event.notificator.model;

import event.notificator.kafka.EventStatus;
import event.notificator.kafka.Registration;

import java.time.LocalDateTime;
import java.util.List;

public record Event(
        Long id,
        String name,
        Long ownerId,
        int maxPlaces,
        List<Registration> registrationList,
        LocalDateTime date,
        int cost,
        int duration,
        Long locationId,
        EventStatus status) {
}
