package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserRestController {

    @Value("${h2-dataservice.uri}")
    public String dataserviceUri;

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public UserPayload get(@PathVariable("id") int id) {
        log.info("get user where id " + id);
        return restTemplate.getForObject(dataserviceUri + "/users/" + id, UserPayload.class);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public UserPayload update(@PathVariable("id") int id, @Valid @RequestBody UserPayload user) {
        log.info("update user where id " + id);
        return restTemplate.exchange(dataserviceUri + "/users/" + id, HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class).getBody();
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public UserPayload create(@Valid @RequestBody UserPayload user) {
        log.info("create user");
        user.setId(null);
        return restTemplate.exchange(dataserviceUri + "/users/", HttpMethod.PUT, new HttpEntity<>(user), UserPayload.class).getBody();
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        log.info("delete user where id " + id);
        restTemplate.delete(dataserviceUri + "/users/" + id);
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
    //endregion
}
