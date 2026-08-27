package org.example.subsidyplatformtest;

import org.springframework.stereotype.Service;

@Service
public class EligibilityService {

    private final UserRepository userRepository;

    public EligibilityService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Eligibility checkEligibility(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean eligible =
                user.getAge() >= 18 &&
                        user.getGovernmentId() != null &&
                        !user.getGovernmentId().isEmpty() &&
                        user.getSchemeName() != null &&
                        !user.getSchemeName().isEmpty();

        if (eligible) {
            return new Eligibility(
                    user.getId(),
                    true,
                    "User is eligible for verification"
            );
        }

        return new Eligibility(
                user.getId(),
                false,
                "User is not eligible"
        );
    }
}