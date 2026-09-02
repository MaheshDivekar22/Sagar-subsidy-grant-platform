package org.example.subsidyplatformtest;

import java.util.ArrayList;
import java.util.List;

public class EligibilityResult {

    private Long userId;
    private Long schemeId;
    private String schemeName;

    private List<String> passedCriteria = new ArrayList<>();
    private List<String> failedCriteria = new ArrayList<>();

    private boolean eligible;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSchemeId() {
        return schemeId;
    }

    public void setSchemeId(Long schemeId) {
        this.schemeId = schemeId;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public List<String> getPassedCriteria() {
        return passedCriteria;
    }

    public List<String> getFailedCriteria() {
        return failedCriteria;
    }

    public boolean isEligible() {
        return eligible;
    }

    public void setEligible(boolean eligible) {
        this.eligible = eligible;
    }
}