package com.ecom.security_service.exception;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@ControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    public GlobalExceptionHandler(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @ExceptionHandler(value = {ApplicationRuntimeException.class})
    public final ProblemDetail handleLogicalExceptions(ApplicationRuntimeException exception) {
        var exceptionType = exception.getApiExceptionType();
        var message = this.messageSource.getMessage(exceptionType.messageCode, null, Locale.getDefault());
        return ProblemDetail.forStatusAndDetail(exceptionType.httpStatus, message);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public final ProblemDetail handleServerError(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, exception.getMessage());
    }
}
