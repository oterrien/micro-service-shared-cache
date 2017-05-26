package com.ote.test;

import com.ote.test.persistence.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Slf4j
@Validated
public class UserRestController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapperService userMapperService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public UserPayload get(@PathVariable("id") int id) {
        log.info("get user where id " + id);
        return userMapperService.convert(userRepository.findOne(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public UserPayload update(@PathVariable("id") int id, @Valid @RequestBody UserPayload user) {
        log.info("update user where id " + id);

        return userMapperService.convert(userRepository.save(userMapperService.convert(user)));
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public UserPayload create(@Valid @RequestBody UserPayload user) {
        log.info("create user");
        user.setId(null);
        return userMapperService.convert(userRepository.save(userMapperService.convert(user)));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        log.info("delete user where id " + id);
        userRepository.delete(id);
    }
}
