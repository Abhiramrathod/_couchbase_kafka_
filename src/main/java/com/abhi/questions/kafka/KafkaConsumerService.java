package com.abhi.questions.kafka;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;


public class KafkaConsumerService implements CommandLineRunner, AutoCloseable{
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private KafkaConsumer<String, String> consumer;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id}")
    private String groupId;

    @Value("${spring.kafka.topic.name}")
    private String topic;

    public KafkaConsumerService() {
        this.consumer = null; // Initialize with null, will be set in init method
    }

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Collections.singletonList(topic));
    }

    @Override
    public void run(String... args) {
        new Thread(this::listen).start();
    }
    public void listen() {
        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(100));
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("📥 Received from Kafka: {}", record.value());
                }
            }
        } catch (Exception e) {
            logger.error("Error while consuming messages : {}", e.getMessage(), e);
        } finally {
            close();
        }
    }

    @PreDestroy
    public void close() {
        consumer.close();
    }
}
