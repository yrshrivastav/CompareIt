package com.compareit.app.custom_exception;

public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String errMesg) {
        super(errMesg);
    }
}