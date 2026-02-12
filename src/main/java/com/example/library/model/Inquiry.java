package com.example.library.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "inquiry")
public class
Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 문의한 사용자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    // 문의 내용
    @Column(nullable = false, length = 1000)
    private String message;

    // 연체 정보 스냅샷
    private boolean overdue;
    private int overdueDays;
    private int penaltyAmount;

    // 처리 여부 (관리자)
    @Column(nullable = false)
    private boolean resolved;

    // 처리 완료 시각 (관리자 처리 시각)
    @Column
    private LocalDateTime resolvedAt;

    // 생성 시각 (사용자 문의 시각)
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 관리자 답변
    @Column(length = 1000)
    private String adminReply;

    // 사용자 확인
    @Column(nullable = false)
    private boolean userChecked = false;

    // JPA 사용하기 위한 기본 생성자
    protected Inquiry() {}

    public Inquiry(User user, String message, boolean overdue, int overdueDays, int penaltyAmount) {
        this.user = user;
        this.message = message;
        this.overdue = overdue;
        this.overdueDays = overdueDays;
        this.penaltyAmount = penaltyAmount;
        this.createdAt = LocalDateTime.now();
        this.resolved = false;
    }

    // 비즈니스 메서드
    public void markUserChecked() {
        this.userChecked = true;
    }

    public void markResolved() {
        if (this.resolved) {
            return; // 이미 처리된 경우 무시
        }
        this.resolved = true;
        this.resolvedAt = LocalDateTime.now();
    }

    public void reply(String reply) {
        this.adminReply = reply;

        if (!this.resolved) {
            this.resolved = true;
            this.resolvedAt = LocalDateTime.now();
        }
    }

    // Getter
    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public String getMessage() {
        return message;
    }

    public boolean isOverdue() {
        return overdue;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public int getPenaltyAmount() {
        return penaltyAmount;
    }

    public boolean isResolved() {
        return resolved;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getAdminReply() {
        return adminReply;
    }

    public boolean isUserChecked() {
        return userChecked;
    }

    public void setUserChecked(boolean userChecked) {
        this.userChecked = userChecked;
    }
}
