package com.company.project.controller;

import com.company.project.model.ProfileFieldDef;
import com.company.project.repository.FieldOptionRepository;
import com.company.project.repository.ProfileFieldDefRepository;
import com.company.project.repository.UserProfileValueRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/admin/fields")
public class FieldController {
    
    @Autowired
    private ProfileFieldDefRepository profileFieldDefRepository;

    @Autowired
    private FieldOptionRepository fieldOptionRepository;
    
    @Autowired
    private UserProfileValueRepository userProfileValueRepository;
    
    private final List<String> FIELD_TYPES = Arrays.asList(
        "TEXT", "NUMBER", "EMAIL", "PHONE", "DATE", 
        "DROPDOWN", "TEXTAREA", "BOOLEAN"
    );
    
    private final List<String> TARGET_ROLES = Arrays.asList(
        "MODERATOR", "STANDARD"
    );
    
    @GetMapping("/edit/{id}")
    public String editField(@PathVariable Long id, Model model) {
        ProfileFieldDef field = profileFieldDefRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found with id: " + id));
        
        model.addAttribute("field", field);
        model.addAttribute("fieldTypes", FIELD_TYPES);
        model.addAttribute("targetRoles", TARGET_ROLES);
        return "edit-field";
    }
    
    @PostMapping("/update/{id}")
    public String updateField(@PathVariable Long id, 
                             @ModelAttribute ProfileFieldDef updatedField,
                             RedirectAttributes redirectAttributes) {
        ProfileFieldDef existingField = profileFieldDefRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found with id: " + id));
        
        // Update field properties
        existingField.setFieldName(updatedField.getFieldName());
        existingField.setFieldType(updatedField.getFieldType());
        existingField.setTargetRole(updatedField.getTargetRole());
        existingField.setIsRequired(updatedField.getIsRequired());
        
        profileFieldDefRepository.save(existingField);
        
        redirectAttributes.addFlashAttribute("success", "Field updated successfully!");
        return "redirect:/admin/dashboard";
    }
    // Add this method to your existing FieldController class
@DeleteMapping("/{id}")
@ResponseBody
public ResponseEntity<String> deleteFieldAjax(@PathVariable Long id) {
    try {
        ProfileFieldDef field = profileFieldDefRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found"));
        
        String fieldName = field.getFieldName();
        profileFieldDefRepository.deleteById(id);
        
        return ResponseEntity.ok("Field '" + fieldName + "' deleted successfully!");
        
    } catch (Exception e) {
        return ResponseEntity.status(500).body("Error: " + e.getMessage());
    }
}
}