package com.example.library.service;

import com.example.library.model.Loan;

import java.util.List;

public class ChatContext {

    private ChatState state = ChatState.NORMAL;
    private List<Loan> pendingLoans; // 선택 대기 중인 대여 목록

    private boolean fallbackUsed = false;

    public ChatState getState() {
        return state;
    }

    public void setState(ChatState state) {
        this.state = state;
    }

    public List<Loan> getPendingLoans() {
        return pendingLoans;
    }

    public void setPendingLoans(List<Loan> pendingLoans) {
        this.pendingLoans = pendingLoans;
    }

    // fallback 관련
    public boolean isFallbackUsed() {
        return fallbackUsed;
    }

    public void markFallbackUsed() {
        this.fallbackUsed = true;
    }

    public void resetFallback() {
        this.fallbackUsed = false;
    }

    public void reset() {
        this.state = ChatState.NORMAL;
        this.pendingLoans = null;
        this.fallbackUsed = false;
    }
}
