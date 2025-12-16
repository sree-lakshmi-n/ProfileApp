// FieldController.java - Add these methods
package com.company.project.controller;

import com.company.project.model.FieldOption;
import com.company.project.model.ProfileFieldDef;
import com.company.project.model.User;
import com.company.project.model.UserFieldState;
import com.company.project.model.UserProfileValue;
import com.company.project.repository.FieldOptionRepository;
import com.company.project.repository.ProfileFieldDefRepository;
import com.company.project.repository.UserFieldStateRepository;
import com.company.project.repository.UserProfileValueRepository;
import com.company.project.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/fields")
public class FieldController {
    
    @Autowired
    private ProfileFieldDefRepository profileFieldDefRepository;

    @Autowired
    private FieldOptionRepository fieldOptionRepository;
    
    @Autowired
    private UserProfileValueRepository userProfileValueRepository;
    
    @Autowired
    private UserFieldStateRepository userFieldStateRepository;

    @Autowired
    private UserRepository userRepository;
    
    private final List<String> FIELD_TYPES = Arrays.asList(
        "TEXT", "NUMBER", "EMAIL", "PHONE", "DATE", 
        "DROPDOWN", "TEXTAREA", "BOOLEAN"
    );
    
    private final List<String> TARGET_ROLES = Arrays.asList(
        "MODERATOR", "STANDARD"
    );
    
    @GetMapping("/add")
    public String showAddFieldForm(Model model) {
        model.addAttribute("field", new ProfileFieldDef());
        model.addAttribute("fieldTypes", FIELD_TYPES);
        model.addAttribute("targetRoles", TARGET_ROLES);
        model.addAttribute("isEdit", false);
        model.addAttribute("options", new ArrayList<FieldOption>());
        return "field-form";
    }
    
    @PostMapping("/save")
public String saveField(@ModelAttribute ProfileFieldDef fieldData,
                       @RequestParam(value = "optionValues[]", required = false) List<String> optionValues,
                       @RequestParam(value = "displayLabels[]", required = false) List<String> displayLabels,
                       RedirectAttributes redirectAttributes) {
    try {
        List<ProfileFieldDef> existingFields = profileFieldDefRepository.findAll();
        boolean fieldExists = existingFields.stream()
            .anyMatch(f -> f.getFieldName().equalsIgnoreCase(fieldData.getFieldName()) 
                         && f.getTargetRole().equals(fieldData.getTargetRole()));
        
        if (fieldExists) {
            redirectAttributes.addFlashAttribute("error", 
                "A field with name '" + fieldData.getFieldName() + "' already exists for " + 
                fieldData.getTargetRole() + " role.");
            return "redirect:/admin/fields/add";
        }
        
        if (fieldData.getIsRequired() == null) {
            fieldData.setIsRequired(false);
        }
        
        ProfileFieldDef savedField = profileFieldDefRepository.save(fieldData);
        markFieldAsChangedForRoleUsers(savedField);

        
        if ("DROPDOWN".equals(savedField.getFieldType()) && optionValues != null && displayLabels != null) {
            saveFieldOptions(savedField.getId(), optionValues, displayLabels);
        }
        
        redirectAttributes.addFlashAttribute("success", 
            "Field '" + savedField.getFieldName() + "' added successfully for " + savedField.getTargetRole() + " role!");
        
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", 
            "Error adding field: " + e.getMessage());
        return "redirect:/admin/fields/add";
    }
    
    return "redirect:/admin/dashboard";
}
    
    @GetMapping("/edit/{id}")
    public String editField(@PathVariable Long id, Model model) {
        ProfileFieldDef field = profileFieldDefRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found with id: " + id));
        
        List<FieldOption> options = fieldOptionRepository.findByFieldDefId(id);
        
        model.addAttribute("field", field);
        model.addAttribute("fieldTypes", FIELD_TYPES);
        model.addAttribute("targetRoles", TARGET_ROLES);
        model.addAttribute("isEdit", true);
        model.addAttribute("options", options);
        return "field-form";
    }
    
    @PostMapping("/update/{id}")
    public String updateField(@PathVariable Long id, 
                             @ModelAttribute ProfileFieldDef updatedField,
                             @RequestParam(value = "optionValues[]", required = false) List<String> optionValues,
                             @RequestParam(value = "displayLabels[]", required = false) List<String> displayLabels,
                             @RequestParam(value = "deleteOptionIds[]", required = false) List<Long> deleteOptionIds,
                             RedirectAttributes redirectAttributes) {
        ProfileFieldDef existingField = profileFieldDefRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found with id: " + id));
        
        if (!existingField.getFieldName().equalsIgnoreCase(updatedField.getFieldName()) 
            || !existingField.getTargetRole().equals(updatedField.getTargetRole())) {
            
            List<ProfileFieldDef> existingFields = profileFieldDefRepository.findAll();
            boolean conflict = existingFields.stream()
                .filter(f -> !f.getId().equals(id))
                .anyMatch(f -> f.getFieldName().equalsIgnoreCase(updatedField.getFieldName()) 
                             && f.getTargetRole().equals(updatedField.getTargetRole()));
            
            if (conflict) {
                redirectAttributes.addFlashAttribute("error", 
                    "A field with name '" + updatedField.getFieldName() + "' already exists for " + 
                    updatedField.getTargetRole() + " role.");
                return "redirect:/admin/fields/edit/" + id;
            }
        }
        
        boolean fieldTypeChanged = !existingField.getFieldType().equals(updatedField.getFieldType());
        boolean isRequiredChanged = existingField.getIsRequired() != updatedField.getIsRequired();
        
        existingField.setFieldName(updatedField.getFieldName());
        existingField.setFieldType(updatedField.getFieldType());
        existingField.setTargetRole(updatedField.getTargetRole());
        existingField.setIsRequired(updatedField.getIsRequired());
        
        existingField = profileFieldDefRepository.save(existingField);
        
        if ("DROPDOWN".equals(updatedField.getFieldType())) {
            if (deleteOptionIds != null && !deleteOptionIds.isEmpty()) {
                deleteOptionIds.forEach(optionId -> fieldOptionRepository.deleteById(optionId));
            }
            
            if (optionValues != null && displayLabels != null) {
                saveFieldOptions(id, optionValues, displayLabels);
            }
        } else {
            if (fieldTypeChanged && "DROPDOWN".equals(existingField.getFieldType())) {
                fieldOptionRepository.deleteByFieldDefId(id);
            }
        }
        
        if (fieldTypeChanged || isRequiredChanged) {
            updateUserValuesForFieldChange(existingField, fieldTypeChanged);
        }
        
        markFieldAsChangedForRoleUsers(existingField);
        
        redirectAttributes.addFlashAttribute("success", "Field " + updatedField.getFieldName() + " of " + updatedField.getTargetRole() + " role updated successfully!");
        return "redirect:/admin/dashboard";
    }

    private void saveFieldOptions(Long fieldDefId, List<String> optionValues, List<String> displayLabels) {
        fieldOptionRepository.deleteByFieldDefId(fieldDefId);
        
        for (int i = 0; i < optionValues.size(); i++) {
            if (optionValues.get(i) != null && !optionValues.get(i).trim().isEmpty()) {
                FieldOption option = new FieldOption(
                    fieldDefId,
                    optionValues.get(i).trim(),
                    displayLabels.get(i) != null ? displayLabels.get(i).trim() : optionValues.get(i).trim()
                );
                fieldOptionRepository.save(option);
            }
        }
    }
    
    private void updateUserValuesForFieldChange(ProfileFieldDef field, boolean fieldTypeChanged) {
        List<UserProfileValue> existingValues = userProfileValueRepository.findByFieldDefId(field.getId());
        
        for (UserProfileValue value : existingValues) {
            if (fieldTypeChanged) {
                value.setFieldValue(getDefaultValueForFieldType(field));
            } else if (field.getIsRequired() && (value.getFieldValue() == null || value.getFieldValue().isEmpty())) {
                value.setFieldValue(getDefaultValueForFieldType(field));
            }
            userProfileValueRepository.save(value);
        }
    }
    
    private String getDefaultValueForFieldType(ProfileFieldDef field) {
        return switch (field.getFieldType()) {
            case "NUMBER" -> "0";
            case "BOOLEAN" -> "false";
            case "DATE" -> LocalDateTime.now().toLocalDate().toString();
            default -> "";
        };
    }
    
   private void markFieldAsChangedForRoleUsers(ProfileFieldDef field) {
    List<User> users = userRepository.findAll().stream()
        .filter(user -> user.getRole().name().equals(field.getTargetRole()))
        .collect(Collectors.toList());
    
    for (User user : users) {
        Optional<UserFieldState> existingState = userFieldStateRepository
            .findByUserIdAndFieldDefId(user.getId(), field.getId());
        
        UserFieldState state;
        if (existingState.isPresent()) {
            state = existingState.get();
            if (!state.getLastSeenVersion().equals(field.getVersion())) {
                state.setHasChanges(true);
            }
        } else {
            state = new UserFieldState(user.getId(), field.getId(), field.getVersion() - 1);
            state.setHasChanges(true);
        }
        
        state.setLastCheckedAt(LocalDateTime.now());
        userFieldStateRepository.save(state);
    }
}
    
    @DeleteMapping("/{id}")
    @ResponseBody
    @Transactional
    public ResponseEntity<String> deleteFieldAjax(@PathVariable Long id) {
        try {
            ProfileFieldDef field = profileFieldDefRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Field not found"));
            
            String fieldName = field.getFieldName();
            System.out.println("----here");
            System.out.println(fieldName);
            String role = field.getTargetRole();
            
            fieldOptionRepository.deleteByFieldDefId(id);
            
            userFieldStateRepository.deleteByFieldDefId(id);
            
            profileFieldDefRepository.deleteById(id);

            
            return ResponseEntity.ok("Field '" + fieldName + "' deleted successfully for " + role + " role!");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/options/{fieldDefId}")
    @ResponseBody
    public ResponseEntity<List<FieldOption>> getFieldOptions(@PathVariable Long fieldDefId) {
        List<FieldOption> options = fieldOptionRepository.findByFieldDefId(fieldDefId);
        return ResponseEntity.ok(options);
    }
}