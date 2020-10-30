package com.sap.bulletinboard.ads.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.cloud.config.java.AbstractCloudConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cloud")
public class CloudRabbitConfig extends AbstractCloudConfig {

    public static final String STATISTICS_ROUTING_KEY = "statistics.adIsShown";

    /**
     * Parses the local environment variable VCAP_SERVICES (containing cloud information) and provides a
     * ConnectionFactory. The superclass {@link AbstractCloudConfig}, part of the Spring Cloud plugin, is used for this.
     */
    @Bean   
    public ConnectionFactory rabbitConnectionFactory() {
        CachingConnectionFactory factory = (CachingConnectionFactory) (connectionFactory().rabbitConnectionFactory());
        factory.setPublisherConfirms(true);
        factory.setPublisherReturns(true);
        // When using publisher confirms, the cache size needs to be large enough
        // otherwise channels can be closed before confirms are received.
        factory.setChannelCacheSize(100);
        return factory;
    }

    /**
     * Using the ConnectionFactory, provide an AmqpAdmin implementation. This can be used, for example, to declare new
     * queues.
     */
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareQueue(new Queue(STATISTICS_ROUTING_KEY)); // creates queue, if not existing
        return rabbitAdmin;
    }

    /**
     * Using the ConnectionFactory, provide an AmqpTemplate implementation. This can be used, for example, to send
     * messages.
     */
    @Bean(name = "rabbitTemplate")   
    public AmqpTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMandatory(true); // otherwise we get no info whether message could not be routed
        return rabbitTemplate;
    }

}