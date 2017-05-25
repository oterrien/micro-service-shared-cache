package com.ote.test;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ProxyService {

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "readFallback")
    public UserPayload read(Integer id) {
        return restTemplate.getForObject("http://proxy-service/users/" + id, UserPayload.class);
    }

    @HystrixCommand(fallbackMethod = "createFallback")
    public UserPayload create(UserPayload user) {
        return restTemplate.exchange("http://proxy-service/users/", HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class).getBody();
    }

    @HystrixCommand(fallbackMethod = "updateFallback")
    public UserPayload update(Integer id, UserPayload user) {
        return restTemplate.exchange("http://proxy-service/users/" + id, HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class).getBody();
    }

    @HystrixCommand(fallbackMethod = "deleteFallback")
    public void delete(Integer id) {
        restTemplate.delete("http://proxy-service/users/" + id);
    }

    //region fallbacks
    public UserPayload readFallback(Integer id) {
        throw new UnavailableProxyServiceException("Unable to read user for id " + id);
    }

    public UserPayload createFallback(UserPayload user) {
        throw new UnavailableProxyServiceException("Unable to create user " + user.toString());
    }

    public UserPayload updateFallback(Integer id, UserPayload user) {
        throw new UnavailableProxyServiceException("Unable to reset user for id " + id + " : " + user.toString());
    }

    public void deleteFallback(Integer id) {
        throw new UnavailableProxyServiceException("Unable to delete user for id " + id);
    }

    public static class UnavailableProxyServiceException extends RuntimeException {

        public UnavailableProxyServiceException(String message) {
            super(message);
        }
    }
    //endregion
}
