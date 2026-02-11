package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.stereotype.Service;

import io.confluent.kafka.serializers.KafkaAvroSerializer;

@Service
@Configuration
public class KafkaService {

    @Value("${schema.registry.url:http://localhost:8081}")
    String schemaRegistryUrl;

    private final Map<String, KafkaTemplate<String, String>> templateCache = new ConcurrentHashMap<>();

    public void publishDataToKafka(String brokerUrl, String topic, String message, String serializationType) {
        KafkaTemplate<String, String> template = templateCache.computeIfAbsent("kafka",
                k1 -> this.createTemplate(brokerUrl, serializationType));
        template.send(topic, message);
    }

    private KafkaTemplate<String, String> createTemplate(String brokerUrl, String serializationType) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, brokerUrl +":9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props = getSerializerBasedProperties(serializationType, props)  ;          
        ProducerFactory<String, String> factory = new DefaultKafkaProducerFactory<>(props);
        return new KafkaTemplate<>(factory);
    }

    private Map<String, Object> getSerializerBasedProperties(String serializationType,  Map<String, Object> props ) {
        return switch (serializationType.toLowerCase()) {
            case "avro" -> {
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class);
                props.put("schema.registry.url", schemaRegistryUrl);
                props.put("key.converter.schema.registry.url", schemaRegistryUrl);  
                props.put("value.converter.schema.registry.url", schemaRegistryUrl);
                yield props;
            }
            case "json" -> {
                props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                yield props;
            }
            default -> throw new IllegalArgumentException("Unsupported serialization type: " + serializationType);
        };
    }
}
