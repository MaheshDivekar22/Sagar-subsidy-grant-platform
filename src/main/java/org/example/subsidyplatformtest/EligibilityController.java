package org.example.subsidyplatformtest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/eligibility")
public class EligibilityController {

    private final EligibilityService eligibilityService;

    public EligibilityController(EligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }

    @GetMapping("/{userId}")
    public Eligibility checkEligibility(@PathVariable Long userId) {
        return eligibilityService.checkEligibility(userId);
    }
}