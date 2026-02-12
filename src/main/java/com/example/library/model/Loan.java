package com.example.library.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 여러 대출 -> 한 사용자
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // 여러 대출 -> 한 도서
    @ManyToOne(optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 반납 여부 (true = 완료, false = 현재 대여 중)
    private boolean returned;

    // 대여일
    private LocalDate loanDate;

    // 반납 기한
    private LocalDate dueDate;

    // 연장 횟수 (0 = 아직 연장 안 함)
    private int extensionCount;

    // JPA 사용하기 위한 기본 생성자
    protected Loan() {}

    // 대여 생성자
    public Loan(User user, Book book) {
        this.user = user;
        this.book = book;
        this.returned = false;
        this.extensionCount = 0;
        this.loanDate = LocalDate.now();
        this.dueDate = loanDate.plusDays(14); // 대여 기간 14일

        book.loan(); // Book 상태 변경
    }

    // 비즈니스 메서드
    public void returnBook() {
        this.returned = true;
        this.book.returnBook();
    }

    public boolean isOverdue() {
        return !returned && dueDate != null && dueDate.isBefore(LocalDate.now());
    }

    public boolean canExtend() {
        return extensionCount < 1 && !returned && !isOverdue();
    }

    public void extend() {
            if (isOverdue()) {
                throw new IllegalStateException("연체 중에는 대출 연장이 불가능합니다.");
            }

            if (extensionCount >= 1) {
                throw new IllegalStateException("이미 연장한 대출입니다.");
            }

            if (returned) {
                throw new IllegalStateException("반납한 도서는 연장할 수 없습니다.");
            }

            this.dueDate = this.dueDate.plusDays(14);
            this.extensionCount++;
    }

    // Getter / Setter
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Book getBook() {
        return book;
    }

    public boolean isReturned() {
        return returned;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }
}
