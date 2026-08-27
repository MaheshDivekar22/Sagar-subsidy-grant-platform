package org.example.subsidyplatformtest;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/beneficiary")
public class BeneficiaryController {

    private final BeneficiaryService beneficiaryService;

    public BeneficiaryController(BeneficiaryService beneficiaryService) {
        this.beneficiaryService = beneficiaryService;
    }

    @GetMapping("/verify/{userId}")
    public Beneficiary verifyBeneficiary(@PathVariable Long userId) {
        return beneficiaryService.verifyBeneficiary(userId);
    }
}