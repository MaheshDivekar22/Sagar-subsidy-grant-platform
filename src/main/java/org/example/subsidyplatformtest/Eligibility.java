package org.example.subsidyplatformtest;

public class Eligibility {

    private Long userId;
    private boolean eligible;
    private String message;

    public Eligibility(Long userId, boolean eligible, String message) {
        this.userId = userId;
        this.eligible = eligible;
        this.message = message;
    }

    public Long getUserId() {
        return userId;
    }

    public boolean isEligible() {
        return eligible;
    }

    public String getMessage() {
        return message;
    }
}