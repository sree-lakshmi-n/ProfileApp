// ProfileFieldDef.java - ensure it has default constructor and proper annotations
package com.company.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profile_field_def")
public class ProfileFieldDef {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "field_name", nullable = false, length = 100)
    private String fieldName;
    
    @Column(name = "field_type", nullable = false, length = 20)
    private String fieldType;
    
    @Column(name = "target_role", nullable = false, length = 20)
    private String targetRole;
    
    @Column(name = "is_required")
    private Boolean isRequired = false;
    
    // Default constructor for Spring form binding
    public ProfileFieldDef() {
    }
    
    // Constructor for convenience
    public ProfileFieldDef(String fieldName, String fieldType, String targetRole, Boolean isRequired) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.targetRole = targetRole;
        this.isRequired = isRequired;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }
    
    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }
    
    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }
    
    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }
}