package event.notificator.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import event.notificator.kafka.EventChangeMessage;
import event.notificator.kafka.EventKafkaEvent;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    private Map<String, Object> getCommonConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        return props;
    }

    @Bean
    public ConsumerFactory<Long, EventKafkaEvent> eventKafkaEventConsumerFactory() {
        Map<String, Object> props = getCommonConsumerConfigs();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "events");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);

        DefaultKafkaConsumerFactory<Long, EventKafkaEvent> consumerFactory = new DefaultKafkaConsumerFactory<>(props);
        consumerFactory.setValueDeserializer(new JsonDeserializer<>(EventKafkaEvent.class, false));

        return consumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<Long, EventKafkaEvent> eventKafkaEventListenerContainerFactory(
            ConsumerFactory<Long, EventKafkaEvent> consumerFactory
    ) {
        final ConcurrentKafkaListenerContainerFactory<Long, EventKafkaEvent> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        return factory;
    }

    @Bean
    public ConsumerFactory<String, EventChangeMessage> eventChangeMessageConsumerFactory() {
        Map<String, Object> props = getCommonConsumerConfigs();
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "events-changes");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        DefaultKafkaConsumerFactory<String, EventChangeMessage> consumerFactory = new DefaultKafkaConsumerFactory<>(props);

        consumerFactory.setValueDeserializer(new JsonDeserializer<>(EventChangeMessage.class, false));

        return consumerFactory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EventChangeMessage>
    eventChangeMessageListenerContainerFactory(
            ConsumerFactory<String, EventChangeMessage> consumerFactory
    ) {
        final ConcurrentKafkaListenerContainerFactory<String, EventChangeMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);

        return factory;
    }
}
