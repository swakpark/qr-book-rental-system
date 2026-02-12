package com.example.library.exception;

import com.example.library.controller.BookController;
import com.example.library.controller.LoanController;
import com.example.library.controller.UserController;
import org.springframework.http.HttpStatus;
import com.example.library.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice(assignableTypes = { BookController.class, UserController.class, LoanController.class })
public class GlobalExceptionHandler {

    // ==========================
    // API 전용 Exception
    // - 로그인 / 회원 / 챗봇
    // - JSON 응답
    // ==========================
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleUserNotFound(UserNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // ==========================
    // View 전용 Exception
    // - QR 도서 페이지
    // - 대여/반납 결과 화면
    // ==========================
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleBookNotFound(BookNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // ==========================
    // View 전용 Exception
    // - QR 도서 페이지
    // - 대여/반납 결과 화면
    // ==========================
    @ExceptionHandler(LoanNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleLoanNotFound(LoanNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // ==========================
    // API 전용 Exception
    // - 로그인 / 회원 / 챗봇
    // - JSON 응답
    // ==========================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity
                .badRequest()
                .body(ApiResponse.validationError("입력값이 올바르지 않습니다.", errors));
    }

    // ==========================
    // View 전용 Exception
    // - QR 도서 페이지
    // - 대여/반납 결과 화면
    // ==========================
    @ExceptionHandler(BookAlreadyLoanedException.class)
    public ResponseEntity<ApiResponse<?>> handleBookAlreadyLoaned(BookAlreadyLoanedException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }
}
