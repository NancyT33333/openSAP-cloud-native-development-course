package com.sap.bulletinboard.ads.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.sap.bulletinboard.ads.controllers.BadRequestException;
import com.sap.bulletinboard.ads.services.UserServiceClient.User;

public class GetUserCommand extends HystrixCommand<User> {
    // Hystrix uses a default timeout of 1000 ms, increase in case you run into problems in remote locations
    private static final int DEFAULT_TIMEOUT_MS = 1000;

    private String url;
    private RestTemplate restTemplate;
    private Logger logger;
    
    public GetUserCommand(String url, RestTemplate restTemplate) {
        super(HystrixCommandGroupKey.Factory.asKey("User"), DEFAULT_TIMEOUT_MS);
        this.url = url;
        this.restTemplate = restTemplate;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    protected User run() throws Exception {
      ResponseEntity<User> responseEntity = restTemplate.getForEntity(url, User.class);
      
      logger.info("received response, status code: {}", responseEntity.getStatusCode());
      if  (!responseEntity.getStatusCode().is2xxSuccessful()) {          
          throw new BadRequestException("Not received 2xx status code");
      }
      return responseEntity.getBody();
      
    }

    // this will be used in exercise 18
    protected int getTimeoutInMs() {
        return this.properties.executionTimeoutInMilliseconds().get();
    }
}