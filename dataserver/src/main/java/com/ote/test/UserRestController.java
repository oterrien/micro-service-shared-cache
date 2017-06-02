package com.ote.test;

import com.ote.test.persistence.UserEntity;
import com.ote.test.persistence.UserPersistenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload get(@PathVariable("id") int id) {
        log.info("get user where id " + id);
        simulateLongProcess();
        UserPayload userPayload = userMapperService.convert(userPersistenceService.find(id));
        return userPayload;
    }

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public Page<UserPayload> get(@ModelAttribute UserPayload userPayloadFilter,
                                 @RequestParam(required = false) String sortingBy,
                                 @RequestParam(required = false, defaultValue = "ASC") String sortingDirection,
                                 @RequestParam(required = false, defaultValue = "${page.default.size}") int pageSize,
                                 @RequestParam(required = false, defaultValue = "0") int pageIndex) {

        log.info("get user where filter is " + userPayloadFilter);
        simulateLongProcess();
        Specification<UserEntity> filter = userMapperService.getFilter(userPayloadFilter);
        Pageable pageRequest = userMapperService.getPageRequest(sortingBy, sortingDirection, pageSize, pageIndex);
        Page<UserPayload> page = userPersistenceService.find(filter, pageRequest).map(p -> userMapperService.convert(p));
        log.info(page.getContent().toString());
        return page;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK) // PAY ATTENTION TO NOT USE RESET_CONTENT which does not allow any response body
    @ResponseBody
    public UserPayload update(@PathVariable("id") int id, @Valid @RequestBody UserPayload user) {
        log.info("update user where id " + id);
        simulateLongProcess();
        return userMapperService.convert(userPersistenceService.reset(id, userMapperService.convert(user)));
    }

    @RequestMapping(value = "", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public UserPayload create(@Valid @RequestBody UserPayload user) {
        log.info("create user");
        return userMapperService.convert(userPersistenceService.create(userMapperService.convert(user)));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") int id) {
        log.info("delete user where id " + id);
        simulateLongProcess();
        userPersistenceService.delete(id);
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public void delete(@ModelAttribute UserPayloadFilter userPayloadFilter) {

        log.info("delete user where filter is " + userPayloadFilter);
        simulateLongProcess();
        userPersistenceService.delete(userMapperService.getFilter(userPayloadFilter));
    }

    @ExceptionHandler(value = {UserPersistenceService.NotFoundException.class})
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String handleNotFoundException(UserPersistenceService.NotFoundException ex) {
        return ex.getMessage();
    }

    private void simulateLongProcess() {
        try {
            Thread.sleep(3000);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
