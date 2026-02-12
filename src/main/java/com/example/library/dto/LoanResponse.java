package com.example.library.dto;

import com.example.library.model.Loan;

public class LoanResponse {

    private Long loanId;
    private Long userId;
    private String userName;
    private Long bookId;
    private String bookTitle;
    private boolean returned;

    public LoanResponse(Long loanId, Long userId, String userName, Long bookId, String bookTitle, boolean returned) {
        this.loanId = loanId;
        this.userId = userId;
        this.userName = userName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.returned = returned;
    }

    public static LoanResponse from(Loan loan) {
        return new LoanResponse(
                loan.getId(),
                loan.getUser().getId(),
                loan.getUser().getName(),
                loan.getBook().getId(),
                loan.getBook().getTitle(),
                loan.isReturned()
        );
    }

    // Getter
    public Long getLoanId() {
        return loanId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public Long getBookId() {
        return bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public boolean isReturned() {
        return returned;
    }
}
