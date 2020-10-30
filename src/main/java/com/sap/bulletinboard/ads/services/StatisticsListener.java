package com.sap.bulletinboard.ads.services;

import java.nio.charset.Charset;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component // defines a Spring Bean with name "statisticsListener"
@Profile("cloud")
public class StatisticsListener implements MessageListener {

    
    @Override
    public void onMessage(Message message) {
        Logger logger = LoggerFactory.getLogger(getClass());

        logger.info("statistic message received: {}", toString(message.getBody()));
    }

    private String toString(byte[] byteArray) {
        return new String(byteArray, Charset.forName("UTF-8"));
    }
}
