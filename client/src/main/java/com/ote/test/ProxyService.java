package com.ote.test;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ProxyService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "readFallback")
    public ResponseEntity<UserPayload> read(Integer id) {
        return restTemplate.getForEntity("http://proxy-service/users/" + id, UserPayload.class);
    }

    @HystrixCommand(fallbackMethod = "createFallback")
    public ResponseEntity<UserPayload> create(UserPayload user) {
        return restTemplate.exchange("http://proxy-service/users/", HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class);
    }

    @HystrixCommand(fallbackMethod = "updateFallback")
    public ResponseEntity<UserPayload> update(Integer id, UserPayload user) {
        return restTemplate.exchange("http://proxy-service/users/" + id, HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class);
    }

    @HystrixCommand(fallbackMethod = "deleteFallback")
    public ResponseEntity<Void> delete(Integer id) {
        return restTemplate.exchange("http://proxy-service/users/" + id, HttpMethod.DELETE, null, Void.class);
    }

    //region fallbacks
    private ResponseEntity<UserPayload> readFallback(Integer id) {
        throw new UnavailableProxyServiceException("Unable to read user for id " + id);
    }

    private ResponseEntity<UserPayload> createFallback(UserPayload user) {
        throw new UnavailableProxyServiceException("Unable to create user " + user.toString());
    }

    private ResponseEntity<UserPayload> updateFallback(Integer id, UserPayload user) {
        throw new UnavailableProxyServiceException("Unable to reset user for id " + id + " : " + user.toString());
    }

    private ResponseEntity<Void> deleteFallback(Integer id) {
        throw new UnavailableProxyServiceException("Unable to delete user for id " + id);
    }

    public static class UnavailableProxyServiceException extends RuntimeException {

        public UnavailableProxyServiceException(String message) {
            super(message);
        }
    }
    //endregion
}
