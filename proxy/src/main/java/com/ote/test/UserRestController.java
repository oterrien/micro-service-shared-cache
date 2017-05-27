package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

@RestController
@RequestMapping("/users")
@Slf4j
@CacheConfig(cacheNames = "users")
public class UserRestController {

    @Value("${dataservice.uri}")
    private String dataserviceUri;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @Cacheable(key = "#id")
    public UserPayload get(@PathVariable("id") int id) {
        log.info("get user where id " + id);
        return restTemplate.getForObject(dataserviceUri + "/users/" + id, UserPayload.class);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @CachePut(key = "#id")
    public UserPayload update(@PathVariable("id") int id, @RequestBody UserPayload user) throws Exception {
        try {
            log.info("update user where id " + id);
            return restTemplate.exchange(dataserviceUri + "/users/" + id, HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class).getBody();
        } finally {
            log.warn("updated");
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserPayload create(@RequestBody UserPayload user) {
        log.info("create user");
        user.setId(null);
        return restTemplate.exchange(dataserviceUri + "/users/", HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class).getBody();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @CacheEvict(key = "#id")
    public void delete(@PathVariable("id") int id) {
        log.info("delete user where id " + id);
        restTemplate.exchange(dataserviceUri + "/users/" + id, HttpMethod.DELETE, null, Void.class);
    }

    //region exception handlers
    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleValidationFailure(ConstraintViolationException ex) {

        StringBuilder messages = new StringBuilder();
        ex.getConstraintViolations().
                forEach(p -> messages.append(p.getMessage()).append("\n"));
        return messages.toString();
    }

    @ExceptionHandler(value = {HttpClientErrorException.class})
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }
    //endregion
}
