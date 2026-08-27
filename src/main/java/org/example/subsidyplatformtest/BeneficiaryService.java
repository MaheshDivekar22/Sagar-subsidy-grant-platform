package org.example.subsidyplatformtest;

import org.springframework.stereotype.Service;

@Service
public class BeneficiaryService {

    private final UserRepository userRepository;

    public BeneficiaryService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Beneficiary verifyBeneficiary(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        boolean verified =
                user.getGovernmentId() != null &&
                        !user.getGovernmentId().isEmpty() &&
                        user.getAge() >= 18 &&
                        user.getContactNumber() != null &&
                        !user.getContactNumber().isEmpty();

        if (verified) {
            return new Beneficiary(
                    user.getId(),
                    true,
                    "VERIFIED",
                    "Beneficiary data verified successfully"
            );
        }

        return new Beneficiary(
                user.getId(),
                false,
                "REJECTED",
                "Beneficiary verification failed"
        );
    }
}