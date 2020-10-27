package com.sap.bulletinboard.ads.services;

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixCommandProperties;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.sap.bulletinboard.ads.controllers.BadRequestException;
import com.sap.bulletinboard.ads.services.UserServiceClient.User;

public class GetUserCommand extends HystrixCommand<User> {
    // Hystrix uses a default timeout of 1000 ms, increase in case you run into problems in remote locations
    private static final int DEFAULT_TIMEOUT_MS = 1000;

    private String url;
    private RestTemplate restTemplate;
    private Logger logger;
    private Supplier<User> fallbackFunction;
    
    
    public GetUserCommand(String url, RestTemplate restTemplate, Supplier<User> fallbackFunction) {
        super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("User"))
                .andCommandKey(HystrixCommandKey.Factory.asKey("User.getById")).andCommandPropertiesDefaults(
                        HystrixCommandProperties.Setter().withExecutionTimeoutInMilliseconds(1500)));
        this.fallbackFunction = fallbackFunction;
        this.url = url;
        this.restTemplate = restTemplate;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    protected User run() throws Exception {
        try {
            ResponseEntity<User> responseEntity = sendRequest();
            logger.info("received response, status code: {}", responseEntity.getStatusCode());
            return responseEntity.getBody();

        } catch (HttpClientErrorException error) {
            logger.error("received HTTP status code: {}", error.getStatusCode());
            throw new HystrixBadRequestException("Unsuccessful request", error);
        } catch(HttpServerErrorException error) {
            logger.warn("received HTTP status code: {}", error.getStatusCode());
            throw error;
        } 

      
    }

    @Override
    protected User getFallback() {
        boolean bErrTimeOut = isResponseTimedOut();
        boolean bErrFailedExec = isFailedExecution();
        boolean bErrRej = isResponseRejected();
        if (bErrTimeOut) {
            logger.error("Response TimedOut");
        } else if (bErrFailedExec) {
            logger.error("Execution Failed");
        } else if (bErrRej) {
            logger.error("Resposce rejected");
        }
//        User defaultUser = new User();
//        defaultUser.premiumUser = false;
        User fallbackUser = fallbackFunction.get();
//        return defaultUser;
        return fallbackUser;
               

    }

    protected ResponseEntity<User> sendRequest() {
        ResponseEntity<User> responseEntity = restTemplate.getForEntity(url, User.class);
        return responseEntity;
    }

    // this will be used in exercise 18
    protected int getTimeoutInMs() {
        return this.properties.executionTimeoutInMilliseconds().get();
    }
}