package com.rabobank.statementprocessor.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@NoArgsConstructor
@Getter
@JsonInclude(NON_NULL)
public class CommonResponse<T> {

    private String result;

    private String message;

    private List<T> errorRecords;

    public CommonResponse(String result) {
        this.result = result;
        this.errorRecords = Collections.emptyList();
    }

    public CommonResponse(String result, String message) {
        this(result);
        this.message = message;
    }

    public CommonResponse(String result, List<T> errorRecords) {
        this(result);
        this.errorRecords = errorRecords;
    }
}
