package com.ecom.security_service.exception;

public class ApplicationRuntimeException extends BaseRuntimeException{

    public ApplicationRuntimeException(ApiExceptionType apiExceptionType) {
        super(apiExceptionType);
    }
}
