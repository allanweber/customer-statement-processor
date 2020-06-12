package com.rabobank.statementprocessor.validation.exception;

public class InvalidStatementJsonParseException extends Exception {
    private static final long serialVersionUID = -547596976740421167L;

    public InvalidStatementJsonParseException(String message) {
        super(message);
    }
}
