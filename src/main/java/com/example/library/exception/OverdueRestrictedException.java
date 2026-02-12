package com.example.library.exception;

public class OverdueRestrictedException extends RuntimeException {

    private final int overdueDays;
    private final int penaltyAmount;

    public OverdueRestrictedException(int overdueDays, int penaltyAmount) {
        super("연체로 인해 대여가 제한되었습니다.");
        this.overdueDays = overdueDays;
        this.penaltyAmount = penaltyAmount;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public int getPenaltyAmount() {
        return penaltyAmount;
    }
}
