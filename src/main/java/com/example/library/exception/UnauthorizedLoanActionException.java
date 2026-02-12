package com.example.library.exception;

public class UnauthorizedLoanActionException extends RuntimeException{
    public UnauthorizedLoanActionException(String message) {
        super(message);
    }
}
