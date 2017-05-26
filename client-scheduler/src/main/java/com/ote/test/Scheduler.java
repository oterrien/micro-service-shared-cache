package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class Scheduler {

    @Autowired
    private ProxyService proxyService;

    @Scheduled(fixedRate = 5000)
    private void callProxy() {
        try {
            log.info(proxyService.read(2).toString());
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }
}
