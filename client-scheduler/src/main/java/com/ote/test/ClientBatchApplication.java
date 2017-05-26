package com.ote.test;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCircuitBreaker
@EnableScheduling
public class ClientBatchApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientBatchApplication.class).run(args);
    }


}