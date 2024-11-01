package com.ecom.security_service.model;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;


@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, property = "@class")
@Data
@AllArgsConstructor
public class UserSessionModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String email;
    private Boolean loggedIn;
    private Boolean sessionValid;
}
