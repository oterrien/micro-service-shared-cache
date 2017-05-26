package com.ote.test;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ProxyService {

    @Value("${proxy-service.uri}")
    private String proxyUri;

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "readFallback")
    public UserPayload read(Integer id) {
        return restTemplate.getForObject("http://"+proxyUri+"/users/" + id, UserPayload.class);
    }

    @HystrixCommand(fallbackMethod = "createFallback")
    public UserPayload create(UserPayload user) {
        return restTemplate.exchange("http://"+proxyUri+"/users/", HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class).getBody();
    }

    @HystrixCommand(fallbackMethod = "updateFallback")
    public UserPayload update(Integer id, UserPayload user) {
        return restTemplate.exchange("http://"+proxyUri+"/users/" + id, HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class).getBody();
    }

    @HystrixCommand(fallbackMethod = "deleteFallback")
    public void delete(Integer id) {
        restTemplate.delete("http://"+proxyUri+"/users/" + id);
    }

    //region fallbacks
    private UserPayload readFallback(Integer id) {
        throw new UnavailableProxyServiceException("Unable to read user for id " + id);
    }

    private UserPayload createFallback(UserPayload user) {
        throw new UnavailableProxyServiceException("Unable to create user " + user.toString());
    }

    private UserPayload updateFallback(Integer id, UserPayload user) {
        throw new UnavailableProxyServiceException("Unable to reset user for id " + id + " : " + user.toString());
    }

    private void deleteFallback(Integer id) {
        throw new UnavailableProxyServiceException("Unable to delete user for id " + id);
    }

    public static class UnavailableProxyServiceException extends RuntimeException {

        public UnavailableProxyServiceException(String message) {
            super(message);
        }
    }
    //endregion
}
