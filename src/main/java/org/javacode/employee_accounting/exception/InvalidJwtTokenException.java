package org.javacode.employee_accounting.exception;

public class InvalidJwtTokenException extends Exception {
    public InvalidJwtTokenException(String message) {
        super(message);
    }
}
