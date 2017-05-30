package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.*;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.ConstraintViolationException;

@RestController
@RequestMapping("/users")
@CacheConfig(cacheNames = "users")
@Slf4j
public class UserRestController {

    @Value("${dataservice.uri}")
    private String dataserviceUri;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @Cacheable(key = "#id")
    public UserPayload get(@PathVariable("id") int id) {
        log.info("get user where id " + id);
        return restTemplate.getForObject(dataserviceUri + "/users/" + id, UserPayload.class);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public MyPage<UserPayload> getMany(@ModelAttribute UserPayload userPayloadFilter,
                                       @RequestParam(required = false) String sortingBy,
                                       @RequestParam(required = false) String sortingDirection,
                                       @RequestParam(required = false) Integer pageSize,
                                       @RequestParam(required = false) Integer pageIndex) {

        log.info("get user where filter is " + userPayloadFilter);

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(dataserviceUri + "/users/")
                .queryParam("sortingBy", sortingBy)
                .queryParam("sortingDirection", sortingDirection)
                .queryParam("pageSize", pageSize)
                .queryParam("pageIndex", pageIndex)
                .queryParams(CollectionUtils.toMultiValueMap(userPayloadFilter.getFilterMap()));

        return restTemplate.getForObject(builder.build().encode().toUri(), MyPage.class);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @CachePut(key = "#id")
    public UserPayload update(@PathVariable("id") int id, @RequestBody UserPayload user) throws Exception {
        try {
            log.info("update user where id " + id);
            return restTemplate.exchange(dataserviceUri + "/users/" + id, HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class).getBody();
        } finally {
            log.warn("updated");
        }
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload create(@RequestBody UserPayload user) {
        log.info("create user");
        user.setId(null);
        return restTemplate.exchange(dataserviceUri + "/users/", HttpMethod.POST, new HttpEntity<>(user), UserPayload.class).getBody();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
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
    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handleHttpClientErrorException(HttpClientErrorException ex) {
        return new ResponseEntity<>(ex.getMessage(), ex.getStatusCode());
    }
    //endregion
}
