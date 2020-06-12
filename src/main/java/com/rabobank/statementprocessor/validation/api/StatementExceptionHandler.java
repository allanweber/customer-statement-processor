package com.rabobank.statementprocessor.validation.api;

import com.rabobank.statementprocessor.dto.CommonResponse;
import com.rabobank.statementprocessor.validation.dto.StatementError;
import com.rabobank.statementprocessor.validation.exception.InvalidStatementException;
import com.rabobank.statementprocessor.validation.exception.InvalidStatementJsonParseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@RestControllerAdvice
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
public class StatementExceptionHandler {

    @ExceptionHandler(InvalidStatementJsonParseException.class)
    @ResponseStatus(BAD_REQUEST)
    public CommonResponse<Object> handleInvalidStatementJsonParseException(InvalidStatementJsonParseException ex) {
        log.error("Invalid Statement Json Parse", ex);
        return new CommonResponse<>(BAD_REQUEST.name(),ex.getMessage());
    }

    @ExceptionHandler(InvalidStatementException.class)
    @ResponseStatus(OK)
    public CommonResponse<StatementError> handleInvalidStatementJsonParseException(InvalidStatementException ex) {
        log.error("Invalid Statement Record", ex);
        List<StatementError> statementErrors = ex.getRecords().stream().map(StatementError::mapTo).collect(Collectors.toList());
        return new CommonResponse<>(ex.getStatus().getLabel(), statementErrors);
    }
}
