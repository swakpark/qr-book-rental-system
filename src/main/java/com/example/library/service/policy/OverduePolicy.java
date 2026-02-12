package com.example.library.service.policy;

public class OverduePolicy {

    private final boolean canBorrow;
    private final int overdueDays;
    private final int penaltyAmount;

    public OverduePolicy(boolean canBorrow, int overdueDays, int penaltyAmount) {
        this.canBorrow = canBorrow; // 대여할 수 있는지 (true/false)
        this.overdueDays = overdueDays; // 연체 일
        this.penaltyAmount = penaltyAmount; // 연체료
    }

    public boolean canBorrow() {
        return canBorrow;
    }

    public int getOverdueDays() {
        return overdueDays;
    }

    public int getPenaltyAmount() {
        return penaltyAmount;
    }
}
