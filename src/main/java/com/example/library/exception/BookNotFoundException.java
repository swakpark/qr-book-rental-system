package com.example.library.exception;

public class BookNotFoundException extends RuntimeException{

    // ID 받는 생성자 (Long)
    public BookNotFoundException(Long bookId) {
        super("도서를 찾을 수 없습니다. id=" + bookId);
    }

    // 커스템 메시지를 직접 받는 생성자
    public BookNotFoundException(String message) {
        super(message);
    }
}
