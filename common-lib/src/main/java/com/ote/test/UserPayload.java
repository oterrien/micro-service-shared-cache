package com.ote.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UserPayload implements Serializable {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("first_name")
    @NotNull
    private String firstName;

    @JsonProperty("last_name")
    @NotNull
    private String lastName;

    @JsonIgnore
    public Map<String, List<String>> getFilterMap() {

        Map<String, List<String>> map = new HashMap<>();
        map.put("firstName", Arrays.asList(firstName));
        map.put("lastName", Arrays.asList(lastName));
        return map;
    }
}
