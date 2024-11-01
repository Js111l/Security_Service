package com.ecom.security_service.model;

import java.io.Serializable;

public record LoginStatusResponse(Boolean loggedIn)
implements Serializable {
}