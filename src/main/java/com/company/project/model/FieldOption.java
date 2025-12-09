package com.company.project.model;
import jakarta.persistence.*;

@Entity
@Table(name = "field_option")
public class FieldOption {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "field_def_id")
    private Long fieldDefId;
    
    @Column(name = "option_value")
    private String optionValue;

    @Column(name = "display_label")
    private String displayLabel;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getFieldDefId() { return fieldDefId; }
    public void setFieldDefId(Long fieldDefId) { this.fieldDefId = fieldDefId; }
    
    public String getOptionValue() { return optionValue; }
    public void setOptionValue(String optionValue) { this.optionValue = optionValue; }

    public String getDisplayLabel() { return displayLabel; }
    public void setDisplayLabel(String displayLabel) { this.displayLabel = displayLabel; }
    
}
