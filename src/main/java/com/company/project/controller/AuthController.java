package com.company.project.controller;

import com.company.project.model.*;
import com.company.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class AuthController {
    
    @Autowired
    private ProfileFieldDefRepository profileFieldDefRepository;
    
    @Autowired
    private FieldOptionRepository fieldOptionRepository;
    
    @Autowired
    private UserProfileValueRepository userProfileValueRepository;
    
    @Autowired
    private UserFieldStateRepository userFieldStateRepository;
    
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
    
    private void checkForFieldChanges(User currentUser, String targetRole, Model model) {
        Long userId = currentUser.getId();
        List<ProfileFieldDef> fields = profileFieldDefRepository.findByTargetRole(targetRole);
        List<String> changedFields = new ArrayList<>();
        boolean hasFieldChanges = false;
        
        for (ProfileFieldDef field : fields) {
            Optional<UserFieldState> userFieldStateOpt = userFieldStateRepository.findByUserIdAndFieldDefId(userId, field.getId());
            
            if (userFieldStateOpt.isPresent()) {
                UserFieldState userFieldState = userFieldStateOpt.get();
                if (!userFieldState.getLastSeenVersion().equals(field.getVersion())) {
                    changedFields.add(field.getFieldName());
                    hasFieldChanges = true;
                    
                    // Update user's last seen version
                    userFieldState.setLastSeenVersion(field.getVersion());
                    userFieldState.setHasChanges(true);
                    userFieldState.setLastCheckedAt(LocalDateTime.now());
                    userFieldStateRepository.save(userFieldState);
                }
            } else {
                // First time seeing this field
                UserFieldState newState = new UserFieldState(userId, field.getId(), field.getVersion());
                userFieldStateRepository.save(newState);
            }
        }
        
        model.addAttribute("hasFieldChanges", hasFieldChanges);
        model.addAttribute("changedFields", changedFields);
    }
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "ADMIN");
        
        List<ProfileFieldDef> fieldDefinitions = profileFieldDefRepository.findAll();
        Comparator<ProfileFieldDef> comparator = Comparator
            .comparing(ProfileFieldDef::getTargetRole)
            .thenComparing(ProfileFieldDef::getId);
        List<ProfileFieldDef> sortedFields = fieldDefinitions.stream()
            .sorted(comparator)
            .toList();
        model.addAttribute("fields", sortedFields);
        
        return "admin-dashboard";
    }
    
    @GetMapping("/moderator/dashboard")
    public String moderatorDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "MODERATOR");
        
        // Check for field changes
        checkForFieldChanges(currentUser, "MODERATOR", model);
        
        // Fetch only MODERATOR fields
        List<ProfileFieldDef> moderatorFields = profileFieldDefRepository.findByTargetRole("MODERATOR");
        List<ProfileFieldVM> vmList = moderatorFields.stream()
            .map(f -> new ProfileFieldVM(
                f.getId(), f.getFieldName(), f.getFieldType(),
                f.getIsRequired(), f.getTargetRole(),
                fieldOptionRepository.findByFieldDefId(f.getId())
            ))
            .toList();
        model.addAttribute("fields", vmList);

        // Load existing values
        Long userId = currentUser.getId();
        Map<Long, String> values = new HashMap<>();
        List<UserProfileValue> rows = userProfileValueRepository.findByUserId(userId);
        for (var row : rows) {
            values.put(row.getFieldDefId(), row.getFieldValue());
        }
        model.addAttribute("values", values);
        
        return "moderator-dashboard";
    }
    
    @GetMapping("/standard/dashboard")
    public String standardDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "STANDARD");
        
        // Check for field changes
        checkForFieldChanges(currentUser, "STANDARD", model);
        
        // Fetch only STANDARD fields
        List<ProfileFieldDef> standardFields = profileFieldDefRepository.findByTargetRole("STANDARD");
        List<ProfileFieldVM> vmList = standardFields.stream()
            .map(f -> new ProfileFieldVM(
                f.getId(), f.getFieldName(), f.getFieldType(),
                f.getIsRequired(), f.getTargetRole(),
                fieldOptionRepository.findByFieldDefId(f.getId())
            ))
            .toList();
        model.addAttribute("fields", vmList);

        // Load existing values
        Long userId = currentUser.getId();
        Map<Long, String> values = new HashMap<>();
        List<UserProfileValue> rows = userProfileValueRepository.findByUserId(userId);
        for (var row : rows) {
            values.put(row.getFieldDefId(), row.getFieldValue());
        }
        model.addAttribute("values", values);
        
        return "standard-dashboard";
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
    
    @PostMapping("/acknowledge-changes")
    public String acknowledgeChanges(@RequestParam("fieldIds") List<Long> fieldIds, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        for (Long fieldDefId : fieldIds) {
            Optional<UserFieldState> userFieldStateOpt = userFieldStateRepository.findByUserIdAndFieldDefId(currentUser.getId(), fieldDefId);
            if (userFieldStateOpt.isPresent()) {
                UserFieldState userFieldState = userFieldStateOpt.get();
                userFieldState.setHasChanges(false);
                userFieldStateRepository.save(userFieldState);
            }
        }
        
        redirectAttributes.addFlashAttribute("success", "Changes acknowledged!");
        return "redirect:/dashboard";
    }
}