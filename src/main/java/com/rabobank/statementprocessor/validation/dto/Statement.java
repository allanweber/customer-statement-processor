package com.rabobank.statementprocessor.validation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Getter
@Builder
public class Statement {

    @NotNull(message = "Enter the reference")
    private Long reference;

    @NotBlank(message = "Enter the account")
    private String account;

    @NotBlank(message = "Enter the description")
    private String description;

    @NotNull(message = "Enter the start_balance")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @JsonProperty("start_balance")
    private BigDecimal startBalance;

    @NotNull(message = "Enter the mutation")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    private BigDecimal mutation;

    @NotNull(message = "Enter the end_balance")
    @Digits(integer = Integer.MAX_VALUE, fraction = 2)
    @JsonProperty("end_balance")
    private BigDecimal endBalance;

    @Override
    public boolean equals(Object o) {
        boolean isEqual;
        if (this == o) {
            isEqual = true;
        }
        if (o == null || getClass() != o.getClass()) {
            isEqual = false;
        } else {
            Statement statement = (Statement) o;
            isEqual = reference.equals(statement.reference);
        }
        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(reference);
    }
}
