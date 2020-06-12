package com.rabobank.statementprocessor.validation.exception;

import com.rabobank.statementprocessor.validation.dto.Statement;
import com.rabobank.statementprocessor.validation.dto.StatementStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
@Getter
public class InvalidStatementException extends Exception {
    private static final long serialVersionUID = -4978288029495728802L;

    private final StatementStatus status;

    private final List<Statement> records;
}
