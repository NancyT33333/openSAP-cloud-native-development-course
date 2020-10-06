package com.sap.bulletinboard.ads.controllers;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InconsistentException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public InconsistentException(String message) {
        super(message);
    }
}
