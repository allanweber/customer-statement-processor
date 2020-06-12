package com.rabobank.statementprocessor.validation.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabobank.statementprocessor.ObjectMapperProvider;
import com.rabobank.statementprocessor.StatementHelper;
import com.rabobank.statementprocessor.dto.CommonResponse;
import com.rabobank.statementprocessor.validation.dto.Statement;
import com.rabobank.statementprocessor.validation.dto.StatementError;
import com.rabobank.statementprocessor.validation.dto.StatementStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class StatementValidationControllerIntegratedTest {

    private final ObjectReader responseErrorReader = ObjectMapperProvider.get().readerFor(new TypeReference<CommonResponse<StatementError>>() {});
    private final ObjectWriter statementListWriter = ObjectMapperProvider.get().writerFor(new TypeReference<List<Statement>>() {});

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void Given_ValidStatements_When_Validate_Then_AssertSuccess() throws Exception {
        var statements = StatementHelper.getValid(5);

        var payload = statementListWriter.writeValueAsString(statements);
        var response = mockMvc.perform(post("/validation")
                .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        CommonResponse<StatementError> commonResponse = responseErrorReader.readValue(response.getContentAsString());
        assertEquals(StatementStatus.SUCCESSFUL.getLabel(), commonResponse.getResult());
        assertTrue(commonResponse.getErrorRecords().isEmpty());
        assertNull(commonResponse.getMessage());
    }

    @Test
    public void Given_DuplicateStatements_When_Validate_Then_AssertDuplicateReference() throws Exception {
        var statements = StatementHelper.getValid(5);
        var duplicateStatement = StatementHelper.validStatement(1);
        var secondDuplicateStatement = StatementHelper.validStatement(4);
        statements.add(duplicateStatement);
        statements.add(secondDuplicateStatement);

        var payload = statementListWriter.writeValueAsString(statements);
        var response = mockMvc.perform(post("/validation")
                .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        CommonResponse<StatementError> commonResponse = responseErrorReader.readValue(response.getContentAsString());
        assertEquals(StatementStatus.DUPLICATE_REFERENCE.getLabel(), commonResponse.getResult());
        assertEquals(2,commonResponse.getErrorRecords().size());
        assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(1L))));
        assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(4L))));
        assertNull(commonResponse.getMessage());
    }

    @Test
    public void Given_InvalidEndBalanceStatements_When_Validate_Then_AssertDuplicateReference() throws Exception {
        var statements = StatementHelper.getValid(5);
        var invalidBalanceStatement = Statement.builder().reference(11L).account("NL").description("any")
                .startBalance(BigDecimal.valueOf(10.01D))
                .mutation(BigDecimal.valueOf(-10.01D))
                .endBalance(BigDecimal.valueOf(10.01D)).build();
        var secondInvalidBalanceStatement = Statement.builder().reference(12L).account("NL").description("any")
                .startBalance(BigDecimal.valueOf(1D))
                .mutation(BigDecimal.valueOf(1D))
                .endBalance(BigDecimal.valueOf(1D)).build();
        statements.add(invalidBalanceStatement);
        statements.add(secondInvalidBalanceStatement);

        var payload = statementListWriter.writeValueAsString(statements);
        var response = mockMvc.perform(post("/validation")
                .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        CommonResponse<StatementError> commonResponse = responseErrorReader.readValue(response.getContentAsString());
        assertEquals(StatementStatus.INCORRECT_END_BALANCE.getLabel(), commonResponse.getResult());
        assertEquals(2,commonResponse.getErrorRecords().size());
        assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(11L))));
        assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(12L))));
        assertNull(commonResponse.getMessage());
    }

    @Test
    public void Given_InvalidEndBalanceAndDuplicateStatements_When_Validate_Then_AssertDuplicateReference() throws Exception {
        var statements = StatementHelper.getValid(5);
        var invalidBalanceStatement = Statement.builder().reference(11L).account("NL").description("any")
                .startBalance(BigDecimal.valueOf(10.01D))
                .mutation(BigDecimal.valueOf(-10.01D))
                .endBalance(BigDecimal.valueOf(10.01D)).build();
        var duplicateStatement = StatementHelper.validStatement(1);
        statements.add(invalidBalanceStatement);
        statements.add(duplicateStatement);

        var payload = statementListWriter.writeValueAsString(statements);
        var response = mockMvc.perform(post("/validation")
                .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        CommonResponse<StatementError> commonResponse = responseErrorReader.readValue(response.getContentAsString());
        assertEquals(StatementStatus.DUPLICATE_AND_BALANCE.getLabel(), commonResponse.getResult());
        assertEquals(2,commonResponse.getErrorRecords().size());
        assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(1L))));
        assertThat(commonResponse.getErrorRecords(), hasItem(hasProperty("reference", is(11L))));
        assertNull(commonResponse.getMessage());
    }
}
