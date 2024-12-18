package ru.backspark.test.sazonov.exception;

import org.springframework.http.HttpStatus;

public class FileException extends AbstractException {
    public FileException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
