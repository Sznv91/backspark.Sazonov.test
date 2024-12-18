package ru.backspark.test.sazonov.exception;

import org.springframework.http.HttpStatus;

public class IncorrectInputException extends AbstractException {
    public IncorrectInputException(String message, HttpStatus httpStatus) {
        super(message, httpStatus);
    }
}
