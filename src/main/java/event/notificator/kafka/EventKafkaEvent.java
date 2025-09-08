package event.notificator.kafka;

import event.notificator.model.Event;

public record EventKafkaEvent (
        Long eventId,
        EventType eventType,
        Event event
){
}
