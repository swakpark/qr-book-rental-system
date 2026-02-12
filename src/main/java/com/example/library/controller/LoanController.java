package com.example.library.controller;

import com.example.library.dto.ApiResponse;
import com.example.library.dto.LoanResponse;
import com.example.library.model.Loan;
import com.example.library.model.User;
import com.example.library.security.AuthUtil;
import com.example.library.service.LoanService;
import com.example.library.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/loans")
public class LoanController {

    private final LoanService loanService;
    private final UserService userService;

    public LoanController(LoanService loanService, UserService userService) {
        this.loanService = loanService;
        this.userService = userService;
    }

    // 도서 대여 API
    @PostMapping
    public ResponseEntity<ApiResponse<LoanResponse>> loanBook(@RequestParam Long bookId) {

        // 🔐 로그인 체크
        if (!AuthUtil.isLoggedIn()) {
            return ResponseEntity
                    .status(401)
                    .body(ApiResponse.fail("로그인이 필요합니다."));
        }

        // 🔐 로그인 사용자 가져오기
        User user = userService.getUserByEmail(AuthUtil.getEmail());

        Loan loan = loanService.loanBook(user, bookId);

        return ResponseEntity.ok(
                ApiResponse.success(LoanResponse.from(loan))
        );
    }

    // 도서 반납 API (bookId 기준)
    @PostMapping("/return")
    public ResponseEntity<ApiResponse<String>> returnBook(@RequestParam Long bookId) {

        if (!AuthUtil.isLoggedIn()) {
            return ResponseEntity
                    .status(401)
                    .body(ApiResponse.fail("로그인이 필요합니다."));
        }

        User user = userService.getUserByEmail(AuthUtil.getEmail());

        loanService.returnBook(user, bookId);
        return ResponseEntity.ok(ApiResponse.success("도서 반납 완료"));
    }

    // 전체 대여 목록 (관리자 전용으로 쓰는 게 좋음)
    @GetMapping
    public ResponseEntity<ApiResponse<List<LoanResponse>>> getAllLoans() {

        List<LoanResponse> loans = loanService.getAllLoans()
                .stream()
                .map(LoanResponse::from)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(loans));
    }
}
