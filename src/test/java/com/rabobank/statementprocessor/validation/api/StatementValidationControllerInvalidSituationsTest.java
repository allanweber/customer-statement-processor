package com.rabobank.statementprocessor.validation.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.rabobank.statementprocessor.ObjectMapperProvider;
import com.rabobank.statementprocessor.dto.CommonResponse;
import com.rabobank.statementprocessor.validation.dto.Statement;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatementValidationControllerInvalidSituationsTest {

    private final ObjectReader responseErrorReader = ObjectMapperProvider.get().readerFor(CommonResponse.class);
    private final ObjectWriter statementWriter = ObjectMapperProvider.get().writerFor(Statement.class);
    private final ObjectWriter statementListWriter = ObjectMapperProvider.get().writerFor(new TypeReference<List<Statement>>() {});

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void given_InvalidMediaType_When_PostValidation_Then_AssertInternalServerErrorStatus() throws Exception {
        var response = mockMvc.perform(post("/validation")
                .content("any content").contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isInternalServerError())
                .andReturn().getResponse();

        CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());

        assertEquals("Content type 'text/plain' not supported", commonResponse.getMessage());
    }

    @Test
    public void given_SimpleObject_When_PostValidation_Then_AssertBadRequestStatus() throws Exception {

        var statement = Statement.builder()
                .reference(1L)
                .account("NL")
                .description("anything")
                .startBalance(BigDecimal.valueOf(20.00D))
                .mutation(BigDecimal.valueOf(20.00D))
                .endBalance(BigDecimal.valueOf(20.00D))
                .build();

        var payload = statementWriter.writeValueAsString(statement);

        MockHttpServletResponse response = mockMvc.perform(post("/validation")
                .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());

        assertTrue( commonResponse.getMessage().contains("JSON parse error: Cannot deserialize instance of"));
    }

    @Test
    public void given_EmptyArray_When_PostValidation_Then_AssertBadRequestStatus() throws Exception {

        var payload = "[]";

        var response = mockMvc.perform(post("/validation")
                .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());

        assertEquals("Statement without data", commonResponse.getMessage());
    }

    @Test
    public void given_AllFieldsNull_When_PostValidation_Then_AssertBadRequestStatus() throws Exception {

        var payload = "[{}]";

        var response = mockMvc.perform(post("/validation")
                .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());

        assertTrue( commonResponse.getMessage().contains("validate.statements[0].startBalance"));
    }

    @Test
    public void given_ListOfStatementsMissingOnePropertyEach_When_PostValidation_Then_AssertBadRequestStatus() throws Exception {

        var value = BigDecimal.valueOf(20.00D);

        var statementNoReference = Statement.builder().account("NL").description("any").startBalance(value)
                .mutation(value).endBalance(value).build();

        var statementNoAccount = Statement.builder().reference(1L).description("any").startBalance(value)
                .mutation(value).endBalance(value).build();

        var statementNoDescription = Statement.builder().reference(1L).account("NL").startBalance(value)
                .mutation(value).endBalance(value).build();

        var statementNoStartBalance = Statement.builder().reference(1L).account("NL").description("any")
                .mutation(value).endBalance(value).build();

        var statementNoMutation = Statement.builder().reference(1L).account("NL").description("any").startBalance(value)
                .endBalance(value).build();

        var statementNoEndBalance = Statement.builder().reference(1L).account("NL").description("any").startBalance(value)
                .mutation(value).build();

        var statements = Arrays.asList(statementNoReference, statementNoAccount, statementNoDescription,
                statementNoStartBalance, statementNoMutation, statementNoEndBalance);

        var payload = statementListWriter.writeValueAsString(statements);

        var response = mockMvc.perform(post("/validation")
                .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());

        assertTrue( commonResponse.getMessage().contains("validate.statements[0].reference: Enter the reference"));
        assertTrue( commonResponse.getMessage().contains("validate.statements[1].account: Enter the account"));
        assertTrue( commonResponse.getMessage().contains("validate.statements[2].description: Enter the description"));
        assertTrue( commonResponse.getMessage().contains("validate.statements[3].startBalance: Enter the start_balance"));
        assertTrue( commonResponse.getMessage().contains("validate.statements[4].mutation: Enter the mutation"));
        assertTrue( commonResponse.getMessage().contains("validate.statements[5].endBalance: Enter the end_balance"));
    }

    @Test
    public void given_ListOfInvalidDecimals_When_PostValidation_Then_AssertBadRequestStatus() throws Exception {

        var value = BigDecimal.valueOf(20.00D);

        var validStatement = Statement.builder().reference(1L).account("NL").startBalance(value).description("any")
                .mutation(value).endBalance(value).build();

        var statementInvalidStartBalance = Statement.builder().reference(1L).account("NL").description("any")
                .startBalance(BigDecimal.valueOf(20.123D)).mutation(value).endBalance(value).build();

        var statementInvalidMutation = Statement.builder().reference(1L).account("NL").description("any").startBalance(value)
                .mutation(BigDecimal.valueOf(20.123D)).endBalance(value).build();

        var statementInvalidEndBalance = Statement.builder().reference(1L).account("NL").description("any").startBalance(value)
                .endBalance(BigDecimal.valueOf(20.123D)).mutation(value).build();

        var statements = Arrays.asList(validStatement, statementInvalidStartBalance, statementInvalidMutation, statementInvalidEndBalance);

        var payload = statementListWriter.writeValueAsString(statements);

        var response = mockMvc.perform(post("/validation")
                .content(payload).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn().getResponse();

        CommonResponse<?> commonResponse = responseErrorReader.readValue(response.getContentAsString());

        assertTrue( commonResponse.getMessage().contains("validate.statements[1].startBalance: numeric value out of bounds"));
        assertTrue( commonResponse.getMessage().contains("validate.statements[2].mutation: numeric value out of bounds "));
        assertTrue( commonResponse.getMessage().contains("validate.statements[3].endBalance: numeric value out of bounds"));
    }
}