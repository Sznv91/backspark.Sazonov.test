package ru.backspark.test.sazonov.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractException {
    public NotFoundException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}