package com.vendo.product_service.infrastructure.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
@RequiredArgsConstructor
public class ProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> KAFKA_BOOTSTRAP_SERVERS;

    @Bean
    public ProducerFactory<String, ?> producerFactory() {
        return buildProducerFactory(new JsonSerializer<>());
    }

    @Bean
    public ProducerFactory<String, String> stringProducerFactory() {
        return buildProducerFactory(new StringSerializer());
    }

    @Bean
    public KafkaTemplate<String, ?> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public KafkaTemplate<String, String> stringKafkaTemplate() {
        return new KafkaTemplate<>(stringProducerFactory());
    }

    private <T> ProducerFactory<String, T> buildProducerFactory(Serializer<T> serializer) {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(BOOTSTRAP_SERVERS_CONFIG, KAFKA_BOOTSTRAP_SERVERS);
        configProps.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(VALUE_SERIALIZER_CLASS_CONFIG, serializer.getClass());

        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
