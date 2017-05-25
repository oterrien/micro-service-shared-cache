package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;

@RestController
@RequestMapping("/users")
@Slf4j
@RibbonClient(name = "proxy-service", configuration = ProxyServiceConfiguration.class)
@Validated
public class UserRestController {

    @Autowired
    private ProxyService proxyService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    @ResponseBody
    @ResponseStatus(HttpStatus.FOUND)
    public UserPayload get(@PathVariable("id") int id) {
        log.info("get user where id " + id);
        return proxyService.read(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.RESET_CONTENT)
    public UserPayload update(@PathVariable("id") int id, @Valid @RequestBody UserPayload user) {
        log.info("update user where id " + id);
        return proxyService.update(id, user);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public UserPayload create(@Valid @RequestBody UserPayload user) {
        log.info("create user");
        user.setId(null);
        return proxyService.create(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") int id) {
        log.info("delete user where id " + id);
        proxyService.delete(id);
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

    @ExceptionHandler(value = {ProxyService.UnavailableProxyServiceException.class})
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public String handleValidationFailure(ProxyService.UnavailableProxyServiceException ex) {
        return "Proxy is not available";
    }
    //endregion
}
