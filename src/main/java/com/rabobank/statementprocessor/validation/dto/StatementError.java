package com.rabobank.statementprocessor.validation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class StatementError {

    private Long reference;

    private String accountNumber;

    public static StatementError mapTo(Statement statement) {
        if (statement == null) {
            throw new IllegalArgumentException(Statement.class.getSimpleName());
        }
        return new StatementError(statement.getReference(), statement.getAccount());
    }
}
