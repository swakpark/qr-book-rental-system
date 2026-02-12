package com.example.library.exception;

public class BookAlreadyLoanedException extends RuntimeException{
    public BookAlreadyLoanedException(Long bookId) {
        super("이미 대여 중인 도서입니다. bookId=" + bookId);
    }
}
