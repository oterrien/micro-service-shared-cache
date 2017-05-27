package com.ote.test;

import com.ote.test.persistence.UserEntity;
import com.ote.test.persistence.UserPersistenceService;
import com.ote.test.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserRestController {

    @Autowired
    private UserPersistenceService userPersistenceService;

    @Autowired
    private UserMapperService userMapperService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.FOUND)
    @ResponseBody
    public UserPayload get(@PathVariable("id") int id) {
        log.info("get user where id " + id);
        UserPayload userPayload = userMapperService.convert(userPersistenceService.find(id));
        log.info(userPayload.toString());
        return userPayload;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.FOUND) // PAY ATTENTION TO NOT USE RESET_CONTENT which does not allow any response body
    @ResponseBody
    public UserPayload update(@PathVariable("id") int id, @Valid @RequestBody UserPayload user) {
        try {
            log.info("update user where id " + id);
            return userMapperService.convert(userPersistenceService.reset(id, userMapperService.convert(user)));
        } finally {
            log.warn("updated");
        }
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public UserPayload create(@Valid @RequestBody UserPayload user) {
        log.info("create user");
        return userMapperService.convert(userPersistenceService.create(userMapperService.convert(user)));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        log.info("delete user where id " + id);
        userPersistenceService.delete(id);
    }


    @ExceptionHandler(value = {UserPersistenceService.NotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNotFoundException(UserPersistenceService.NotFoundException ex) {
        return ex.getMessage();
    }
}
