package com.ote.test;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Slf4j
public class ProxyService {

    private static String proxyUri = "http://proxy-service";

    @Autowired
    private RestTemplate restTemplate;

    @HystrixCommand(fallbackMethod = "readFallback")
    public ResponseEntity<UserPayload> read(Integer id) {
        return restTemplate.getForEntity(proxyUri + "/users/" + id, UserPayload.class);
    }

    @HystrixCommand(fallbackMethod = "readManyFallback")
    public ResponseEntity<Page> read(UserPayloadFilter userPayloadFilter,
                                     String sortingBy,
                                     String sortingDirection,
                                     Integer pageSize,
                                     Integer pageIndex) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(proxyUri + "/users/")
                .queryParam("sortingBy", sortingBy)
                .queryParam("sortingDirection", sortingDirection)
                .queryParam("pageSize", pageSize)
                .queryParam("pageIndex", pageIndex)
                .queryParams(CollectionUtils.toMultiValueMap(userPayloadFilter.getFilterMap()));

        return restTemplate.getForEntity(builder.build().encode().toUri(), Page.class);
    }

    @HystrixCommand(fallbackMethod = "createFallback")
    public ResponseEntity<UserPayload> create(UserPayload user) {
        return restTemplate.exchange(proxyUri + "/users", HttpMethod.POST, new HttpEntity<>(user), UserPayload.class);
    }

    @HystrixCommand(fallbackMethod = "updateFallback")
    public ResponseEntity<UserPayload> update(Integer id, UserPayload user) {
        return restTemplate.exchange(proxyUri + "/users/" + id, HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class);
    }

    @HystrixCommand(fallbackMethod = "deleteFallback")
    public ResponseEntity<Void> delete(Integer id) {
        return restTemplate.exchange(proxyUri + "/users/" + id, HttpMethod.DELETE, null, Void.class);
    }

    @HystrixCommand(fallbackMethod = "deleteManyFallback")
    public ResponseEntity<Void> delete(UserPayloadFilter userPayloadFilter) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(proxyUri + "/users/")
                .queryParams(CollectionUtils.toMultiValueMap(userPayloadFilter.getFilterMap()));

        return restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.DELETE, null, Void.class);
    }

    //region fallbacks
    private ResponseEntity<UserPayload> readFallback(Integer id) {
        throw new UnavailableProxyServiceException("Unable to read user for id " + id);
    }

    private ResponseEntity<Page> readManyFallback(UserPayloadFilter userPayloadFilter, String sortingBy, String sortingDirection, Integer pageSize, Integer pageIndex) {
        throw new UnavailableProxyServiceException("Unable to read many users for filter " + userPayloadFilter);
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

    private ResponseEntity<Void> deleteManyFallback(UserPayloadFilter userPayloadFilter) {
        throw new UnavailableProxyServiceException("Unable to delete user for filter " + userPayloadFilter);
    }

    public static class UnavailableProxyServiceException extends RuntimeException {

        public UnavailableProxyServiceException(String message) {
            super(message);
        }
    }
    //endregion
}
