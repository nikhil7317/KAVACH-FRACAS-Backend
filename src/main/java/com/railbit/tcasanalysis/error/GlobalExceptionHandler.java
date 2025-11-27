package com.railbit.tcasanalysis.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = NoSuchElementException.class)
    public ResponseEntity<?> noSuchElementExceptionHandle(Exception e){
        Map<String, String> errorResponse = Map.of(
                "message", e.getLocalizedMessage(),
                "status", HttpStatus.NOT_FOUND.toString()
        );
        log.error("Exception : ", e);
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(value = SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<?> SQLIntegrityConstraintViolationExceptionHandle(Exception e){
        Map<String, String> errorResponse = Map.of(
                "message", e.getLocalizedMessage(),
                "status", HttpStatus.INTERNAL_SERVER_ERROR.toString()
        );
        log.error("Exception : ", e);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseEntity<?> methodArgumentNotValidException(Exception e){
        Map<String, String> errorResponse = Map.of(
                "message", e.getLocalizedMessage(),
                "status", HttpStatus.BAD_REQUEST.toString()
        );
        log.error("Exception : ", e);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> httpRequestMethodNotSupportedException(Exception e){
        Map<String, String> errorResponse = Map.of(
                "message", e.getLocalizedMessage(),
                "status", HttpStatus.METHOD_NOT_ALLOWED.toString()
        );
        log.error("Exception : ", e);
        return new ResponseEntity<>(errorResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> exception(Exception e){
        Map<String, String> errorResponse = Map.of(
                "message", e.getLocalizedMessage(),
                "status", HttpStatus.BAD_REQUEST.toString()
        );
        log.error("Exception : ", e);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
