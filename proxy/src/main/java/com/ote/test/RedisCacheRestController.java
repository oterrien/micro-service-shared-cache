package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/cache")
@Slf4j
public class RedisCacheRestController {

    @Autowired
    private CacheManager cacheManager;

    @RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void cleanCache(@PathVariable("name") String cacheName) {
        Optional.ofNullable(cacheManager.getCache(cacheName)).
                ifPresent(Cache::clear);
    }
}
