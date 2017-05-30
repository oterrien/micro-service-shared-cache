package com.ote.test;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.RibbonClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<UserPayload> get(@PathVariable("id") int id) {
        log.info("get user where id " + id);
        return proxyService.read(id);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<UserPayload> update(@PathVariable("id") int id, @Valid @RequestBody UserPayload user) {
        log.info("update user where id " + id);
        return proxyService.update(id, user);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<UserPayload> create(@Valid @RequestBody UserPayload user) {
        log.info("create user");
        user.setId(null);
        return proxyService.create(user);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable("id") int id) {
        log.info("delete user where id " + id);
        return proxyService.delete(id);
    }

    //region exception handlers
    @ExceptionHandler(value = {ConstraintViolationException.class})
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public String handleValidationException(ConstraintViolationException ex) {

        StringBuilder messages = new StringBuilder();
        ex.getConstraintViolations().
                forEach(p -> messages.append(p.getMessage()).append("\n"));
        return messages.toString();
    }

    @ExceptionHandler(value = {ProxyService.UnavailableProxyServiceException.class})
    @ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
    public String handleUnavailabilityException(ProxyService.UnavailableProxyServiceException ex) {
        return "Proxy is not available";
    }
    //endregion
}
