package com.rabobank.statementprocessor.validation.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum StatementStatus {

    SUCCESSFUL("SUCCESSFUL"),
    DUPLICATE_REFERENCE("DUPLICATE_REFERENCE"),
    INCORRECT_END_BALANCE("INCORRECT_END_BALANCE"),
    DUPLICATE_AND_BALANCE(DUPLICATE_REFERENCE.label + "_" + INCORRECT_END_BALANCE.label);

    @Getter
    private String label;
}
