package com.ote.test;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPayloadFilter extends UserPayload {

    @JsonIgnore
    public Map<String, List<String>> getFilterMap() {

        Map<String, List<String>> map = new HashMap<>();
        map.put("firstName", Arrays.asList(getFirstName()));
        map.put("lastName", Arrays.asList(getLastName()));
        return map;
    }
}
