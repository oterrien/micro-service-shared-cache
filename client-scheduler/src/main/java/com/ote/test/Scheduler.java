package com.ote.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    @Autowired
    private ProxyService proxyService;

    @Scheduled(fixedRate = 5000)
    public void callProxy(){
        proxyService.read(2);
    }
}
