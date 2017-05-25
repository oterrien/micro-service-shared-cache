package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class PingRestController {

    @Value("${spring.application.name}")
    public String name;

    @Value("${server.port}")
    public int port;

    /**
     * used by ribbon's client to ping this service
     */
    @RequestMapping("/")
    public String ping() {
        log.info("ping");
        return name + " #" + port;
    }

}
