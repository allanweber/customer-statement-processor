package com.rabobank.statementprocessor.validation.api;

import com.rabobank.statementprocessor.validation.dto.Statement;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Api(tags = "Validation")
@RequestMapping("/validation")
public interface StatementValidationApi {

    @ApiOperation(notes = "Validates a statement json and return the result of the validation", value = "Validate a statement json")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Successfully validated a statement json"),
            @ApiResponse(code = 400, message = "Error during parsing JSON"),
            @ApiResponse(code = 500, message = "Any other error")
    })
    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    ResponseEntity<?> validate(@RequestBody @Valid List<Statement> statements);
}
