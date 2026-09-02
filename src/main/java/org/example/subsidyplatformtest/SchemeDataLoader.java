package org.example.subsidyplatformtest;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SchemeDataLoader {

    @Bean
    CommandLineRunner loadSchemes(SchemeRepository schemeRepository) {

        return args -> {

            if (schemeRepository.count() == 0) {

                Scheme education = new Scheme();
                education.setSchemeName("Education Subsidy");
                education.setCategory("Education");
                education.setDescription(
                        "Financial assistance for eligible students."
                );
                education.setMinAge(18);
                education.setMaxIncome(500000.0);
                education.setRequiredOccupation("Student");
                education.setActive(true);

                schemeRepository.save(education);


                Scheme farmer = new Scheme();
                farmer.setSchemeName("Farmer Support Grant");
                farmer.setCategory("Agriculture");
                farmer.setDescription(
                        "Financial support for eligible farmers."
                );
                farmer.setMinAge(18);
                farmer.setMaxIncome(400000.0);
                farmer.setRequiredOccupation("Farmer");
                farmer.setActive(true);

                schemeRepository.save(farmer);


                Scheme housing = new Scheme();
                housing.setSchemeName("Housing Assistance Scheme");
                housing.setCategory("Housing");
                housing.setDescription(
                        "Financial assistance for eligible beneficiaries."
                );
                housing.setMinAge(18);
                housing.setMaxIncome(300000.0);
                housing.setActive(true);

                schemeRepository.save(housing);


                Scheme business = new Scheme();
                business.setSchemeName("Women Entrepreneurship Grant");
                business.setCategory("Business");
                business.setDescription(
                        "Support for eligible self-employed beneficiaries."
                );
                business.setMinAge(18);
                business.setMaxIncome(600000.0);
                business.setRequiredOccupation("Self Employed");
                business.setActive(true);

                schemeRepository.save(business);


                Scheme senior = new Scheme();
                senior.setSchemeName("Senior Citizen Assistance");
                senior.setCategory("Social Welfare");
                senior.setDescription(
                        "Financial assistance for senior citizens."
                );
                senior.setMinAge(60);
                senior.setMaxIncome(300000.0);
                senior.setActive(true);

                schemeRepository.save(senior);

                System.out.println("Schemes loaded successfully.");
            }
        };
    }
}