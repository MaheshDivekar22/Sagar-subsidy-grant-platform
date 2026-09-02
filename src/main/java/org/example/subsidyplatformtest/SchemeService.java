package org.example.subsidyplatformtest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchemeService {

    @Autowired
    private SchemeRepository schemeRepository;


    // Get all active subsidy schemes
    public List<Scheme> getActiveSchemes() {

        return schemeRepository.findAll()
                .stream()
                .filter(Scheme::isActive)
                .toList();
    }


    // Get a particular scheme using its ID
    public Scheme getSchemeById(Long id) {

        return schemeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Scheme not found with ID: " + id)
                );
    }


    // Check beneficiary eligibility for the selected scheme
    public EligibilityResult checkEligibility(User user, Scheme scheme) {

        EligibilityResult result = new EligibilityResult();

        result.setUserId(user.getId());
        result.setSchemeId(scheme.getId());
        result.setSchemeName(scheme.getSchemeName());

        boolean allPassed = true;


        // ==========================================
        // 1. AGE CHECK
        // ==========================================

        if (scheme.getMinAge() != null) {

            if (user.getAge() >= scheme.getMinAge()) {

                result.getPassedCriteria().add(
                        "Age >= " + scheme.getMinAge() + " : PASS"
                );

            } else {

                result.getFailedCriteria().add(
                        "Age >= " + scheme.getMinAge() + " : FAIL"
                );

                allPassed = false;
            }
        }


        // ==========================================
        // 2. MAXIMUM AGE CHECK
        // ==========================================

        if (scheme.getMaxAge() != null) {

            if (user.getAge() <= scheme.getMaxAge()) {

                result.getPassedCriteria().add(
                        "Age <= " + scheme.getMaxAge() + " : PASS"
                );

            } else {

                result.getFailedCriteria().add(
                        "Age <= " + scheme.getMaxAge() + " : FAIL"
                );

                allPassed = false;
            }
        }


        // ==========================================
        // 3. INCOME CHECK
        // ==========================================

        if (scheme.getMaxIncome() != null) {

            if (user.getIncome() <= scheme.getMaxIncome()) {

                result.getPassedCriteria().add(
                        "Income <= ₹" + scheme.getMaxIncome() + " : PASS"
                );

            } else {

                result.getFailedCriteria().add(
                        "Income <= ₹" + scheme.getMaxIncome() + " : FAIL"
                );

                allPassed = false;
            }
        }


        // ==========================================
        // 4. OCCUPATION CHECK
        // ==========================================

        if (scheme.getRequiredOccupation() != null
                && !scheme.getRequiredOccupation().isBlank()) {

            if (user.getOccupation() != null
                    && user.getOccupation().equalsIgnoreCase(
                    scheme.getRequiredOccupation())) {

                result.getPassedCriteria().add(
                        "Occupation = "
                                + scheme.getRequiredOccupation()
                                + " : PASS"
                );

            } else {

                result.getFailedCriteria().add(
                        "Occupation = "
                                + scheme.getRequiredOccupation()
                                + " : FAIL"
                );

                allPassed = false;
            }
        }


        // ==========================================
        // 5. GOVERNMENT ID CHECK
        // ==========================================

        if (user.getGovernmentId() != null
                && !user.getGovernmentId().isBlank()) {

            result.getPassedCriteria().add(
                    "Government ID available : PASS"
            );

        } else {

            result.getFailedCriteria().add(
                    "Government ID available : FAIL"
            );

            allPassed = false;
        }


        // ==========================================
        // FINAL ELIGIBILITY RESULT
        // ==========================================

        result.setEligible(allPassed);

        return result;
    }
}