package com.abhi.questions.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.util.Properties;


public class KafkaProducerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private KafkaProducer<String, String> producer;
    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.topic.name}")
    private String topic;
    public KafkaProducerService() {
        this.producer = null; // Will be initialized in the init() method
    }

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        this.producer = new KafkaProducer<>(props);
    }
    public void sendMessage(String message) {
        try {
            producer.send(new ProducerRecord<>(topic, message));
            logger.info("Sent to Kafka: {}", message);
        } catch (Exception e) {
            logger.error("Failed to send message to Kafka with Exception : {}", e.getMessage(), e);
        }
    }

    @PreDestroy
    public void close() {
        producer.close();
    }
}