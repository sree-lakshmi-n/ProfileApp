// FieldController.java - Add these methods
package com.company.project.controller;

import com.company.project.model.ProfileFieldDef;
import com.company.project.repository.ProfileFieldDefRepository;
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
    
    // List of allowed field types
    private final List<String> FIELD_TYPES = Arrays.asList(
        "TEXT", "NUMBER", "EMAIL", "PHONE", "DATE", 
        "DROPDOWN", "TEXTAREA", "BOOLEAN"
    );
    
    // List of allowed roles
    private final List<String> TARGET_ROLES = Arrays.asList(
        "MODERATOR", "STANDARD"
    );
    
    @GetMapping("/add")
    public String showAddFieldForm(Model model) {
        model.addAttribute("field", new ProfileFieldDef());
        model.addAttribute("fieldTypes", FIELD_TYPES);
        model.addAttribute("targetRoles", TARGET_ROLES);
        model.addAttribute("isEdit", false);
        return "field-form";
    }
    
    @PostMapping("/save")
    public String saveField(@ModelAttribute ProfileFieldDef field,
                           RedirectAttributes redirectAttributes) {
        try {
            // Check if field with same name and role already exists
            List<ProfileFieldDef> existingFields = profileFieldDefRepository.findAll();
            boolean fieldExists = existingFields.stream()
                .anyMatch(f -> f.getFieldName().equalsIgnoreCase(field.getFieldName()) 
                             && f.getTargetRole().equals(field.getTargetRole()));
            
            if (fieldExists) {
                redirectAttributes.addFlashAttribute("error", 
                    "A field with name '" + field.getFieldName() + "' already exists for " + 
                    field.getTargetRole() + " role.");
                return "redirect:/admin/fields/add";
            }
            
            if (field.getIsRequired() == null) {
                field.setIsRequired(false);
            }
            
            // Save the new field
            profileFieldDefRepository.save(field);
            
            redirectAttributes.addFlashAttribute("success", 
                "Field '" + field.getFieldName() + "' added successfully!");
            
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
        
        model.addAttribute("field", field);
        model.addAttribute("fieldTypes", FIELD_TYPES);
        model.addAttribute("targetRoles", TARGET_ROLES);
        model.addAttribute("isEdit", true);
        return "field-form";
    }
    
    @PostMapping("/update/{id}")
    public String updateField(@PathVariable Long id, 
                             @ModelAttribute ProfileFieldDef updatedField,
                             RedirectAttributes redirectAttributes) {
        ProfileFieldDef existingField = profileFieldDefRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Field not found with id: " + id));
        
        // Check if name changed and conflicts with existing field
        if (!existingField.getFieldName().equalsIgnoreCase(updatedField.getFieldName()) 
            || !existingField.getTargetRole().equals(updatedField.getTargetRole())) {
            
            List<ProfileFieldDef> existingFields = profileFieldDefRepository.findAll();
            boolean conflict = existingFields.stream()
                .filter(f -> !f.getId().equals(id)) // Exclude current field
                .anyMatch(f -> f.getFieldName().equalsIgnoreCase(updatedField.getFieldName()) 
                             && f.getTargetRole().equals(updatedField.getTargetRole()));
            
            if (conflict) {
                redirectAttributes.addFlashAttribute("error", 
                    "A field with name '" + updatedField.getFieldName() + "' already exists for " + 
                    updatedField.getTargetRole() + " role.");
                return "redirect:/admin/fields/edit/" + id;
            }
        }
        
        // Update field properties
        existingField.setFieldName(updatedField.getFieldName());
        existingField.setFieldType(updatedField.getFieldType());
        existingField.setTargetRole(updatedField.getTargetRole());
        existingField.setIsRequired(updatedField.getIsRequired());
        
        profileFieldDefRepository.save(existingField);
        
        redirectAttributes.addFlashAttribute("success", "Field updated successfully!");
        return "redirect:/admin/dashboard";
    }
    
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