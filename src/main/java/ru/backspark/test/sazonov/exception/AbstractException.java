package ru.backspark.test.sazonov.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public abstract class AbstractException extends RuntimeException {
    private String message;
    private HttpStatus httpStatus;

    public AbstractException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
