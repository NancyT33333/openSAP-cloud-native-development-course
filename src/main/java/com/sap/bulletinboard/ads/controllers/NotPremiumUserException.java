package com.sap.bulletinboard.ads.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
public class NotPremiumUserException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotPremiumUserException(String message) {
        super(message);
    }
}
