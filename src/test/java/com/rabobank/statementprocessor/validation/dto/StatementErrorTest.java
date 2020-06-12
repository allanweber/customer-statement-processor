package com.rabobank.statementprocessor.validation.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StatementErrorTest {

    @Test
    public void given_StatementObject_When_MapToStatementError_Then_AssertStatementError() {
        var statement = Statement.builder()
                .reference(1L)
                .account("NL")
                .description("anything")
                .build();

        var statementError = StatementError.mapTo(statement);

        assertEquals(statement.getAccount(), statementError.getAccountNumber());
        assertEquals(statement.getReference(), statementError.getReference());
    }

    @Test
    public void given_StatementObjectNull_When_MapToStatementError_Then_AssertException() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> StatementError.mapTo(null), "Statement");
    }
}