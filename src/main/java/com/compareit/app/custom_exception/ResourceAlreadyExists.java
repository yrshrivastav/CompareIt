package com.compareit.app.custom_exception;

public class ResourceAlreadyExists extends RuntimeException {
    public ResourceAlreadyExists(String errMesg) {
        super(errMesg);
    }
}
