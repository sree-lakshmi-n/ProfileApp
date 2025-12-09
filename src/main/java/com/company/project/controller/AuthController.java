package com.company.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.company.project.model.ProfileFieldDef;
import com.company.project.repository.ProfileFieldDefRepository;
import java.util.Collection;
import java.util.List;

@Controller
public class AuthController {
    
    @Autowired
    private ProfileFieldDefRepository profileFieldDefRepository;
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/")
    public String homePage() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            
            if (!authorities.isEmpty()) {
                String role = authorities.iterator().next().getAuthority();
                
                return switch (role) {
                    case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
                    case "ROLE_MODERATOR" -> "redirect:/moderator/dashboard";
                    case "ROLE_STANDARD" -> "redirect:/standard/dashboard";
                    default -> "redirect:/login?error";
                };
            }
        }
        return "redirect:/login";
    }
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "ADMIN");
        
        // Fetch all field definitions
        List<ProfileFieldDef> fieldDefinitions = profileFieldDefRepository.findAll();
        model.addAttribute("fields", fieldDefinitions);
        
        return "admin-dashboard";
    }
    
    @GetMapping("/moderator/dashboard")
    public String moderatorDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "MODERATOR");
        
        // Fetch only MODERATOR fields
        List<ProfileFieldDef> moderatorFields = 
            profileFieldDefRepository.findByTargetRole("MODERATOR");
        model.addAttribute("fields", moderatorFields);
        
        return "moderator-dashboard";
    }
    
    @GetMapping("/standard/dashboard")
    public String standardDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "STANDARD");
        
        // Fetch only STANDARD fields
        List<ProfileFieldDef> standardFields = 
            profileFieldDefRepository.findByTargetRole("STANDARD");
        model.addAttribute("fields", standardFields);
        
        return "standard-dashboard";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
}