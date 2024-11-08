package com.ecom.security_service.model;


import java.io.Serial;
import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSessionModel implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String email;
    private Boolean loggedIn;
    private Boolean sessionValid;
    private List<String> roles;
}
