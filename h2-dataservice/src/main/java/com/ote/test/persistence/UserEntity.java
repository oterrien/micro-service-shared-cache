package com.ote.test.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "T_USER")
@Data
@NoArgsConstructor
public class UserEntity {

    @Column(name = "ID")
    @Id
    @GeneratedValue
    private Integer id;

    @Column(name = "FIRST_NAME")
    @JsonProperty("first_name")
    private String firstName;

    @Column(name = "LAST_NAME")
    @JsonProperty("last_name")
    private String lastName;
}
