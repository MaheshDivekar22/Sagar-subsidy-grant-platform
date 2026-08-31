package org.example.subsidyplatformtest;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class WebController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    // Home page → Registration page
    @GetMapping("/")
    public String showHomePage() {
        return "redirect:/register";
    }


    // Registration page
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "register";
    }


    // Process registration
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

            entityUser.setGovernmentId(
                    dto.getGovernmentId().trim().toUpperCase()
            );

            entityUser.setContactNumber(dto.getContactNumber());
            entityUser.setAge(dto.getAge());
            entityUser.setAddress(dto.getAddress());
            entityUser.setSchemeName(dto.getSchemeName());

            // Add these if you have added income and occupation
            entityUser.setIncome(dto.getIncome());
            entityUser.setOccupation(dto.getOccupation());

            userService.registerUser(entityUser);

            return "redirect:/login?success";

        } catch (UserRegistrationException e) {

            model.addAttribute("error", e.getMessage());

            return "register";
        }
    }


    // Login page
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }


    // Dashboard
    @GetMapping("/dashboard")
    public String showDashboard(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
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

        return "dashboard";
    }
}