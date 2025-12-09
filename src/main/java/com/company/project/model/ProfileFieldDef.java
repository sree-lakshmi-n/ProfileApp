// ProfileFieldDef.java - add this constructor
package com.company.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "profile_field_def")
public class ProfileFieldDef {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "field_name")
    private String fieldName;
    
    @Column(name = "field_type")
    private String fieldType;
    
    @Column(name = "target_role")
    private String targetRole;
    
    @Column(name = "is_required")
    private Boolean isRequired;
    
    // Default constructor for Spring
    public ProfileFieldDef() {
    }
    
    // Constructor with parameters
    public ProfileFieldDef(String fieldName, String fieldType, String targetRole, Boolean isRequired) {
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.targetRole = targetRole;
        this.isRequired = isRequired;
    }
    
    // Getters and Setters
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