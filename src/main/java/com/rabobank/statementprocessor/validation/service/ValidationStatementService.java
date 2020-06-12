package com.rabobank.statementprocessor.validation.service;

import com.rabobank.statementprocessor.dto.CommonResponse;
import com.rabobank.statementprocessor.validation.dto.Statement;
import com.rabobank.statementprocessor.validation.dto.StatementStatus;
import com.rabobank.statementprocessor.validation.exception.InvalidStatementException;
import com.rabobank.statementprocessor.validation.exception.InvalidStatementJsonParseException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
public class ValidationStatementService {

    @SneakyThrows
    public CommonResponse<?> validate(@Valid List<Statement> statements) {
        validateStatementList(statements);

        List<Statement> invalidBalanceStatements = new ArrayList<>();
        Set<Statement> uniqueStatements = new HashSet<>();
        Set<Statement> duplicated = statements.stream()
                .peek(validateEndBalance().apply(invalidBalanceStatements))
                .filter(duplicated(uniqueStatements))
                .collect(Collectors.toSet());

        checkValidation(duplicated, invalidBalanceStatements);
        return new CommonResponse<>(StatementStatus.SUCCESSFUL.getLabel());
    }

    public Function<List<Statement>, Consumer<Statement>> validateEndBalance() {
        return invalidBalanceStatements -> statement -> {
            if (isFinalBalanceInValid(statement)) {
                invalidBalanceStatements.add(statement);
            }
        };
    }

    public Predicate<Statement> duplicated(Set<Statement> uniqueStatements) {
        return statement -> !uniqueStatements.add(statement);
    }

    private boolean isFinalBalanceInValid(Statement statement) {
        return !statement.getStartBalance().add(statement.getMutation()).equals(statement.getEndBalance());
    }

    private void checkValidation(Set<Statement> duplicated, List<Statement> invalidBalance) throws InvalidStatementException {
        if (!duplicated.isEmpty() && !invalidBalance.isEmpty()) {
            invalidBalance.addAll(duplicated);
            throw new InvalidStatementException(StatementStatus.DUPLICATE_AND_BALANCE, invalidBalance);
        }
        if (!duplicated.isEmpty()) {
            throw new InvalidStatementException(StatementStatus.DUPLICATE_REFERENCE, new ArrayList<>(duplicated));
        }
        if (!invalidBalance.isEmpty()) {
            throw new InvalidStatementException(StatementStatus.INCORRECT_END_BALANCE, invalidBalance);
        }
    }

    private void validateStatementList(@Valid List<Statement> statements) throws InvalidStatementJsonParseException {
        if (statements == null || statements.isEmpty()) {
            throw new InvalidStatementJsonParseException("Statement without data");
        }
    }
}
