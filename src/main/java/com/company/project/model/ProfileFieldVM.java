package com.company.project.model;

import java.util.List;

public class ProfileFieldVM {
    private Long id;
    private String fieldName;
    private String fieldType;
    private Boolean isRequired;
    private String targetRole;
    private List<FieldOption> options;

    public ProfileFieldVM() {}

    public ProfileFieldVM(Long id, String fieldName, String fieldType,
                          Boolean isRequired, String targetRole,
                          List<FieldOption> options) {
        this.id = id;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
        this.isRequired = isRequired;
        this.targetRole = targetRole;
        this.options = options;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFieldName() { return fieldName; }
    public void setFieldName(String fieldName) { this.fieldName = fieldName; }

    public String getFieldType() { return fieldType; }
    public void setFieldType(String fieldType) { this.fieldType = fieldType; }

    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }

    public String getTargetRole() { return targetRole; }
    public void setTargetRole(String targetRole) { this.targetRole = targetRole; }

    public List<FieldOption> getOptions() { return options; }
    public void setOptions(List<FieldOption> options) { this.options = options; }
}