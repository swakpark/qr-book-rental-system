package com.example.library.qr;

public class DecodedToken {

    private final long bookId;
    private final long issuedAt;
    private final long expiresAt;
    private final String signature;

    public DecodedToken(Long bookId, long issuedAt, long expiresAt, String signature) {
        this.bookId = bookId;
        this.issuedAt = issuedAt; // 현재 시간
        this.expiresAt = expiresAt; // 만료 시간
        this.signature = signature;
    }

    public Long getBookId() {
        return bookId;
    }

    public Long getIssuedAt() {
        return issuedAt;
    }

    public long getExpiresAt() {
        return expiresAt;
    }

    public String getSignature() {
        return signature;
    }
}

