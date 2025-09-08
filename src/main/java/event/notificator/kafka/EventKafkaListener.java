package event.notificator.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EventKafkaListener {
    private static final Logger logger = LoggerFactory.getLogger(EventKafkaListener.class);

    @KafkaListener(topics = "events")
    public void listenEvents(ConsumerRecord<Long, EventKafkaEvent> record) {
        logger.info("Received event: {}", record.value());
    }
}
