package com.rabobank.statementprocessor.config;

import com.rabobank.statementprocessor.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class CommonExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public CommonResponse<Object> handleClientException(Exception ex) {
        log.error("Unexpected Error happened", ex);
        return new CommonResponse<>(INTERNAL_SERVER_ERROR.name(), ex.getMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(BAD_REQUEST)
    public CommonResponse<Object> handleJsonFormatException(Exception ex) {
        log.error("Json Format Exception", ex);
        return new CommonResponse<>(BAD_REQUEST.name(), ex.getMessage());
    }

    @ExceptionHandler({ConstraintViolationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(BAD_REQUEST)
    public CommonResponse<Object> handleConstraintViolationException(Exception ex) {
        log.error("Constraints Violation Exception", ex);
        return new CommonResponse<>(BAD_REQUEST.name(), ex.getMessage());
    }
}
