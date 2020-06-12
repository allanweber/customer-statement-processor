package com.rabobank.statementprocessor.validation.service;

import com.rabobank.statementprocessor.StatementHelper;
import com.rabobank.statementprocessor.dto.CommonResponse;
import com.rabobank.statementprocessor.validation.dto.Statement;
import com.rabobank.statementprocessor.validation.dto.StatementStatus;
import com.rabobank.statementprocessor.validation.exception.InvalidStatementException;
import com.rabobank.statementprocessor.validation.exception.InvalidStatementJsonParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationStatementServiceTest {

    private ValidationStatementService validationStatementService = new ValidationStatementService();

    @Test
    public void Given_NullStatementList_When_Validate_Then_AssertThrowException() {
        Assertions.assertThrows(InvalidStatementJsonParseException.class, () -> validationStatementService.validate(null), "Statement without data");
    }

    @Test
    public void Given_EmptyStatementList_When_Validate_Then_AssertThrowException() {
        List<Statement> statements = emptyList();
        Assertions.assertThrows(InvalidStatementJsonParseException.class, () -> validationStatementService.validate(statements), "Statement without data");
    }

    @Test
    public void Given_DuplicatedStatement_When_Validate_Then_AssertThrowInvalidStatementException() {
        var statements = StatementHelper.getValid(5);
        var duplicateStatement = StatementHelper.validStatement(1);
        statements.add(duplicateStatement);

        InvalidStatementException exception = assertThrows(InvalidStatementException.class, () -> validationStatementService.validate(statements));
        assertEquals(StatementStatus.DUPLICATE_REFERENCE, exception.getStatus());
        assertEquals(1, exception.getRecords().size());
        assertThat(exception.getRecords(), hasItem(hasProperty("reference", is(1L))));
    }

    @Test
    public void Given_InvalidBalanceStatement_When_Validate_Then_AssertThrowInvalidStatementException() {
        var statements = StatementHelper.getValid(5);
        var invalidBalanceStatement = Statement.builder().reference(10L).account("BALANCE").startBalance(new BigDecimal(10))
                .mutation(new BigDecimal(-10)).endBalance(new BigDecimal(20)).build();
        statements.add(invalidBalanceStatement);

        InvalidStatementException exception = assertThrows(InvalidStatementException.class, () -> validationStatementService.validate(statements));
        assertEquals(StatementStatus.INCORRECT_END_BALANCE, exception.getStatus());
        assertEquals(1, exception.getRecords().size());
        assertThat(exception.getRecords(), hasItem(hasProperty("reference", is(10L))));
    }

    @Test
    public void Given_InvalidBalanceAndDuplicatedStatement_When_Validate_Then_AssertThrowInvalidStatementException() {
        var statements = StatementHelper.getValid(5);
        var invalidBalanceStatement = Statement.builder().reference(10L).account("BALANCE").startBalance(new BigDecimal(10))
                .mutation(new BigDecimal(-10)).endBalance(new BigDecimal(20)).build();
        statements.add(invalidBalanceStatement);
        var duplicateStatement = StatementHelper.validStatement(1);
        statements.add(duplicateStatement);

        InvalidStatementException exception = assertThrows(InvalidStatementException.class, () -> validationStatementService.validate(statements));
        assertEquals(StatementStatus.DUPLICATE_AND_BALANCE, exception.getStatus());
        assertEquals(2, exception.getRecords().size());
        assertThat(exception.getRecords(), hasItem(hasProperty("reference", is(1L))));
        assertThat(exception.getRecords(), hasItem(hasProperty("reference", is(10L))));
    }

    @Test
    public void Given_ValidStatements_When_Validate_Then_AssertSuccess() {
        var statements = StatementHelper.getValid(5);

        CommonResponse<?> response = validationStatementService.validate(statements);

        assertEquals(StatementStatus.SUCCESSFUL.getLabel(), response.getResult());
        assertNull(response.getMessage());
        assertTrue(response.getErrorRecords().isEmpty());
    }
}