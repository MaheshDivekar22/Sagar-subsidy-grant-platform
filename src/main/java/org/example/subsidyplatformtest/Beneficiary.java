package org.example.subsidyplatformtest;

public class Beneficiary {

    private Long userId;
    private boolean verified;
    private String status;
    private String message;

    public Beneficiary(Long userId, boolean verified, String status, String message) {
        this.userId = userId;
        this.verified = verified;
        this.status = status;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isVerified() {
        return verified;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}