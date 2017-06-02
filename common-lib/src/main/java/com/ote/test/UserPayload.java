package com.ote.test;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UserPayload implements Serializable {

    private Integer id;

    @NotNull
    private String firstName;

    @NotNull
    private String lastName;
}
