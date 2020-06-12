package com.rabobank.statementprocessor.validation.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StatementTest {

    @Test
    public void given_TwoStatementsWithDifferentReferences_When_CheckingEquals_Then_AssertNotEqual() {

        var statementOne = Statement.builder()
                .reference(1L)
                .account("NL")
                .description("anything")
                .build();

        var statementTwo = Statement.builder()
                .reference(2L)
                .account("NL")
                .description("anything")
                .build();

        assertNotEquals(statementOne, statementTwo);
    }

    @Test
    public void given_SameStatement_When_CheckingEquals_Then_AssertEqual() {

        var statement = Statement.builder()
                .reference(1L)
                .account("NL")
                .description("anything")
                .build();

        assertEquals(statement, statement);
    }

    @Test
    public void given_TwoStatementsOneNull_When_CheckingEquals_Then_AssertNotEqual() {

        var statement = Statement.builder()
                .reference(1L)
                .account("NL")
                .description("anything")
                .build();

        assertNotEquals(statement, null);
    }

    @Test
    public void given_TwoStatementsWithEqualReferences_When_CheckingEquals_Then_AssertEqual() {

        var statementOne = Statement.builder()
                .reference(1L)
                .account("NL")
                .description("anything")
                .build();

        var statementTwo = Statement.builder()
                .reference(1L)
                .account("NL")
                .description("anything")
                .build();

        assertEquals(statementOne, statementTwo);
    }
}