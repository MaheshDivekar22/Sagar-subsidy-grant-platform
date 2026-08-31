package org.example.subsidyplatformtest;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class EligibilityService {

    private final UserRepository userRepository;

    public EligibilityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Eligibility checkEligibility(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, String> criteria = new LinkedHashMap<>();

        boolean agePassed = user.getAge() >= 18;

        boolean idPassed = user.getGovernmentId() != null
                && !user.getGovernmentId().isEmpty();

        boolean schemePassed = user.getSchemeName() != null
                && !user.getSchemeName().isEmpty();

        boolean incomePassed = user.getIncome() <= 500000;

        boolean occupationPassed = user.getOccupation() != null
                && !user.getOccupation().isEmpty();

        criteria.put("age", agePassed ? "PASSED" : "FAILED");
        criteria.put("governmentId", idPassed ? "PASSED" : "FAILED");
        criteria.put("schemeName", schemePassed ? "PASSED" : "FAILED");
        criteria.put("income", incomePassed ? "PASSED" : "FAILED");
        criteria.put("occupation", occupationPassed ? "PASSED" : "FAILED");

        boolean eligible =
                agePassed &&
                        idPassed &&
                        schemePassed &&
                        incomePassed &&
                        occupationPassed;

        String message = eligible
                ? "All eligibility criteria passed"
                : "Eligibility criteria not satisfied";

        return new Eligibility(
                user.getId(),
                eligible,
                criteria,
                message
        );
    }
}