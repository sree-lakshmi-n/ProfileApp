package com.company.project.model;

import jakarta.persistence.*;

@Entity
@Table(name = "field_option")
public class FieldOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "field_def_id", nullable = false)
    private Long fieldDefId;
    
    @Column(name = "option_value", nullable = false, length = 100)
    private String optionValue;
    
    @Column(name = "display_label", nullable = false, length = 100)
    private String displayLabel;
    
    // Constructors
    public FieldOption() {}
    
    public FieldOption(Long fieldDefId, String optionValue, String displayLabel) {
        this.fieldDefId = fieldDefId;
        this.optionValue = optionValue;
        this.displayLabel = displayLabel;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getFieldDefId() { return fieldDefId; }
    public void setFieldDefId(Long fieldDefId) { this.fieldDefId = fieldDefId; }
    
    public String getOptionValue() { return optionValue; }
    public void setOptionValue(String optionValue) { this.optionValue = optionValue; }
    
    public String getDisplayLabel() { return displayLabel; }
    public void setDisplayLabel(String displayLabel) { this.displayLabel = displayLabel; }
}