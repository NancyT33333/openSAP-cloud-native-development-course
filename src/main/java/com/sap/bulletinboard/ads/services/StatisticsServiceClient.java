//package com.sap.bulletinboard.ads.services;
//
//import javax.inject.Inject;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.amqp.core.AmqpTemplate;
//import org.springframework.amqp.core.Message;
//import org.springframework.amqp.core.MessagePostProcessor;
//import org.springframework.stereotype.Component;
//
//import com.netflix.hystrix.HystrixCommand;
//import com.netflix.hystrix.HystrixCommandGroupKey;
//import com.netflix.hystrix.HystrixCommandKey;
//import com.sap.bulletinboard.ads.config.CloudRabbitConfig;
//import com.sap.hcp.cf.logging.common.LogContext;
//
//@Component
//
//public class StatisticsServiceClient {
//
//    private Logger logger;
//
//    private AmqpTemplate rabbitTemplate;
//
//    @Inject
//    public StatisticsServiceClient(AmqpTemplate rabbitTemplate) {
//        this.rabbitTemplate = rabbitTemplate;
//        Logger logger = LoggerFactory.getLogger(getClass());
//        this.logger = logger;
//
//    }
//
//    public void advertisementIsShown(long id) {
//        new HystrixIncCommand(id).queue();
//    }
//
//    private class HystrixIncCommand extends HystrixCommand<Void> {
//        protected final String correlationId;
//        private String id;
//
//        HystrixIncCommand(long id) {
//            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey(StatisticsServiceClient.class.getName()))
//                    .andCommandKey(HystrixCommandKey.Factory.asKey(CloudRabbitConfig.STATISTICS_ROUTING_KEY)));
//            this.id = String.valueOf(id);
//            this.correlationId = LogContext.getCorrelationId();
//        }
//
//        @Override
//        protected Void run() throws Exception {
//            LogContext.initializeContext(correlationId);
//            
//            
//            
//            String sId = String.valueOf(id);
//            Object obj = sId;
//            rabbitTemplate.convertAndSend("statistics.adIsShown", obj, new MessagePostProcessor() {
//                public Message postProcessMessage(Message message) {
//           
//                    message.getMessageProperties().setCorrelationId(correlationId);
//                    return message;
//                }
//            });
//            logger.info("Message sent to statistics service. Ad id: {}", String.valueOf(id));
//            return null;
//
//        }
//        @Override
//        protected Void getFallback() {
//            logger.error("Failed to send message to stat service.");
//            return null;
//        }
//    }
//}
