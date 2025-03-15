package com.abhi.questions.config;

import com.abhi.questions.kafka.KafkaConsumerService;
import com.abhi.questions.kafka.KafkaProducerService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {

    @Bean
    public KafkaProducerService kafkaProducerService() {
        return new KafkaProducerService();
    }

    @Bean
    public KafkaConsumerService kafkaConsumerService() {
        return new KafkaConsumerService();
    }
}