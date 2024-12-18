package ru.backspark.test.sazonov.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(AbstractException.class)
    public ResponseEntity<?> handleAppException(AbstractException ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("Exception", ex.getClass().getSimpleName());
        log.error("Перехватили исключение {}, сообщение:{}", ex.getClass().getSimpleName(), ex.getMessage());
        return new ResponseEntity<>(body, ex.getHttpStatus());
    }


    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDbConstraintException(Exception ex, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("Exception", ex.getClass().getSimpleName());
        body.put("message", "Некорректные значения обновляемых полей. В БД уже существует запись с такими значениями.");

        log.error("Перехватили исключение {}, сообщение:{}", ex.getClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleExceptionAll(Exception ex, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("Exception", ex.getClass().getSimpleName());
        body.put("message", "Неожиданная ошибка. " + ex.getMessage());

        log.error("Перехватили исключение {}, сообщение:{}", ex.getClass().getSimpleName(), ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}