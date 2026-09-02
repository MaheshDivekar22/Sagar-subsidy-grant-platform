package org.example.subsidyplatformtest;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SubsidyApplicationRepository
        extends JpaRepository<SubsidyApplication, Long> {
}