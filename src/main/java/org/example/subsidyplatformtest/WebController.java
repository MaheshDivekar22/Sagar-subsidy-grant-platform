package org.example.subsidyplatformtest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchemeService schemeService;

    @Autowired
    private SubsidyApplicationRepository subsidyApplicationRepository;

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }

    @PostMapping("/register")
    public String registerUserForm(
            @Valid @ModelAttribute("user") UserRegistrationDto dto,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            return "register";
        }

        try {
            User entityUser = new User();
            entityUser.setEmail(dto.getEmail());
            entityUser.setPassword(dto.getPassword());
            entityUser.setGovernmentId(dto.getGovernmentId().trim().toUpperCase());
            entityUser.setContactNumber(dto.getContactNumber());
            entityUser.setAge(dto.getAge());
            entityUser.setAddress(dto.getAddress());
            entityUser.setSchemeName(dto.getSchemeName());
            entityUser.setIncome(dto.getIncome());
            entityUser.setOccupation(dto.getOccupation());

            userService.registerUser(entityUser);

            return "redirect:/login?success";

        } catch (UserRegistrationException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/dashboard")
    public String showDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElse(null);

        if (user == null) {
            return "redirect:/login?error=SessionExpired";
        }

        model.addAttribute("user", user);
        model.addAttribute("schemes", schemeService.getActiveSchemes());

        return "dashboard";
    }

    @GetMapping("/eligibility/{schemeId}")
    public String checkSchemeEligibility(
            @PathVariable Long schemeId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElse(null);

        if (user == null) {
            return "redirect:/login?error=SessionExpired";
        }

        Scheme scheme;

        try {
            scheme = schemeService.getSchemeById(schemeId);
        } catch (RuntimeException e) {
            return "redirect:/dashboard?error=SchemeNotFound";
        }

        EligibilityResult result =
                schemeService.checkEligibility(user, scheme);

        model.addAttribute("user", user);
        model.addAttribute("scheme", scheme);
        model.addAttribute("result", result);

        return "eligibility-result";
    }
    @PostMapping("/apply/{schemeId}")
    public String applyForScheme(
            @PathVariable Long schemeId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        User user = userRepository
                .findByEmail(userDetails.getUsername())
                .orElse(null);

        if (user == null) {
            return "redirect:/login?error=SessionExpired";
        }

        Scheme scheme;

        try {
            scheme = schemeService.getSchemeById(schemeId);
        } catch (RuntimeException e) {
            return "redirect:/dashboard?error=SchemeNotFound";
        }

        EligibilityResult result =
                schemeService.checkEligibility(user, scheme);

        if (!result.isEligible()) {
            model.addAttribute("user", user);
            model.addAttribute("scheme", scheme);
            model.addAttribute("result", result);
            model.addAttribute("error", "You are not eligible for this scheme.");
            return "eligibility-result";
        }

        SubsidyApplication application = new SubsidyApplication();

        application.setUser(user);
        application.setScheme(scheme);
        application.setEligibilityStatus(true);
        application.setApplicationStatus("SUBMITTED");
        application.setApplicationDate(java.time.LocalDateTime.now());

        subsidyApplicationRepository.save(application);

        model.addAttribute("application", application);

        return "application-success";
    }
}