package com.company.project.controller;

import com.company.project.model.*;
import com.company.project.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
    private ProfileFieldDefRepository fieldDefRepo;
    
    @Autowired
    private FieldOptionRepository optionRepo;
    
    @Autowired
    private UserProfileValueRepository profileValueRepo;
    
    @Autowired
    private UserFieldStateRepository fieldStateRepo;
    
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/login";
        }
        
        String role = auth.getAuthorities().iterator().next().getAuthority();
        
        return switch (role) {
            case "ROLE_ADMIN" -> "redirect:/admin/dashboard";
            case "ROLE_MODERATOR" -> "redirect:/moderator/dashboard";
            case "ROLE_STANDARD" -> "redirect:/standard/dashboard";
            default -> "redirect:/login?error";
        };
    }
    
    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", "ADMIN");
        
        List<ProfileFieldDef> allFields = fieldDefRepo.findAll();
        allFields.sort(Comparator
            .comparing(ProfileFieldDef::getTargetRole)
            .thenComparing(ProfileFieldDef::getId));
        
        model.addAttribute("fields", allFields);
        
        return "admin-dashboard";
    }
    
    @GetMapping("/moderator/dashboard")
    public String moderatorDashboard(Model model) {
        return showRoleDashboard(model, "MODERATOR");
    }
    
    @GetMapping("/standard/dashboard")
    public String standardDashboard(Model model) {
        return showRoleDashboard(model, "STANDARD");
    }
    
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "access-denied";
    }
    
    @PostMapping("/acknowledge-changes")
    public String acknowledgeChanges(@RequestParam List<Long> fieldIds, RedirectAttributes redirectAttributes) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        for (Long fieldId : fieldIds) {
            Optional<UserFieldState> stateOpt = fieldStateRepo
                .findByUserIdAndFieldDefId(currentUser.getId(), fieldId);
            
            if (stateOpt.isPresent()) {
                UserFieldState state = stateOpt.get();
                state.setHasChanges(false);
                fieldStateRepo.save(state);
            }
        }
        
        redirectAttributes.addFlashAttribute("message", "Changes acknowledged!");
        return "redirect:/dashboard";
    }
    

    private String showRoleDashboard(Model model, String role) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        model.addAttribute("username", auth.getName());
        model.addAttribute("role", role);
        
        checkFieldChanges(currentUser, role, model);
        
        List<ProfileFieldDef> roleFields = fieldDefRepo.findByTargetRole(role);
        roleFields.sort(Comparator.comparing(ProfileFieldDef::getId));

        model.addAttribute("fields", createFieldViewModels(roleFields));
        model.addAttribute("values", getUserFieldValues(currentUser.getId()));
        
        return role.toLowerCase() + "-dashboard";
    }
    
    private List<ProfileFieldVM> createFieldViewModels(List<ProfileFieldDef> fields) {
        List<ProfileFieldVM> result = new ArrayList<>();
        
        for (ProfileFieldDef field : fields) {
            List<FieldOption> options = optionRepo.findByFieldDefId(field.getId());
            
            ProfileFieldVM vm = new ProfileFieldVM(
                field.getId(),
                field.getFieldName(),
                field.getFieldType(),
                field.getIsRequired(),
                field.getTargetRole(),
                options
            );
            
            result.add(vm);
        }
        
        return result;
    }
    
    private Map<Long, String> getUserFieldValues(Long userId) {
        Map<Long, String> values = new HashMap<>();
        
        List<UserProfileValue> userValues = profileValueRepo.findByUserId(userId);
        for (UserProfileValue value : userValues) {
            values.put(value.getFieldDefId(), value.getFieldValue());
        }
        
        return values;
    }
    
    private void checkFieldChanges(User user, String role, Model model) {
        Long userId = user.getId();
        List<ProfileFieldDef> fields = fieldDefRepo.findByTargetRole(role);
        
        List<String> changedFieldNames = new ArrayList<>();
        boolean hasChanges = false;
        
        for (ProfileFieldDef field : fields) {
            Optional<UserFieldState> stateOpt = fieldStateRepo
                .findByUserIdAndFieldDefId(userId, field.getId());
            
            if (stateOpt.isPresent()) {
                UserFieldState state = stateOpt.get();
                
                if (!state.getLastSeenVersion().equals(field.getVersion())) {
                    changedFieldNames.add(field.getFieldName());
                    hasChanges = true;
                    
                    state.setLastSeenVersion(field.getVersion());
                    state.setHasChanges(true);
                    state.setLastCheckedAt(LocalDateTime.now());
                    fieldStateRepo.save(state);
                }
            } else {
                UserFieldState newState = new UserFieldState(
                    userId, field.getId(), field.getVersion()
                );
                fieldStateRepo.save(newState);
            }
        }
        
        model.addAttribute("hasFieldChanges", hasChanges);
        model.addAttribute("changedFields", changedFieldNames);
    }
}