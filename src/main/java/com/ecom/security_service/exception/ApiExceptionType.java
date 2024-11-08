package com.ecom.security_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ApiExceptionType {

    USER_NOT_AUTHORIZED(HttpStatus.FORBIDDEN, "user.no_access"),
    FAILED_LOGIN(HttpStatus.UNAUTHORIZED,"user.failed_login"),
    EMAIL_ALREADY_USED(HttpStatus.UNPROCESSABLE_ENTITY,"user.email_already_used" );


    final HttpStatus httpStatus;
    final String messageCode;

    ApiExceptionType(HttpStatus httpStatus, String messageCode) {
        this.httpStatus = httpStatus;
        this.messageCode = messageCode;
    }


}
