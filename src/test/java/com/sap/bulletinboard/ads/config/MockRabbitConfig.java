package com.sap.bulletinboard.ads.config;

import org.mockito.Mockito;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class MockRabbitConfig {

    @Bean
    @Primary
    AmqpTemplate rabbitTemplate() {
        return Mockito.mock(RabbitTemplate.class);
    }
}