package com.ecom.security_service.exception;

import lombok.Getter;

@Getter
public abstract class BaseRuntimeException extends RuntimeException {
    private final ApiExceptionType apiExceptionType;

    public BaseRuntimeException(ApiExceptionType apiExceptionType) {
        this.apiExceptionType = apiExceptionType;
    }
}
