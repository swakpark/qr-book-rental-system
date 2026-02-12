package com.example.library.exception;

public class LoanNotFoundException extends RuntimeException{
    public LoanNotFoundException(Long loanId) {
        super("대여 기록을 찾을 수 없습니다. id=" + loanId);
    }
}