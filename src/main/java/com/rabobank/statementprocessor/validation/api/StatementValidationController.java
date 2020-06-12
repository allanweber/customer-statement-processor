package com.rabobank.statementprocessor.validation.api;

import com.rabobank.statementprocessor.validation.dto.Statement;
import com.rabobank.statementprocessor.validation.service.ValidationStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RequiredArgsConstructor
@RestController
@Validated
public class StatementValidationController implements StatementValidationApi {

    private final ValidationStatementService validationStatementService;

    @Override
    public ResponseEntity<?> validate(@Valid List<Statement> statements) {
        return ok(validationStatementService.validate(statements));
    }
}
