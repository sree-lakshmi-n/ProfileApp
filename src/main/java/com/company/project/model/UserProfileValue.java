package com.company.project.model;
import jakarta.persistence.*;

@Entity
@Table(name = "user_profile_value")
public class UserProfileValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "field_def_id")
    private Long fieldDefId;

    @Column(name = "field_value")
    private String fieldValue;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFieldDefId() { return fieldDefId; }
    public void setFieldDefId(Long fieldDefId) { this.fieldDefId = fieldDefId; }

    public String getFieldValue() { return fieldValue; }
    public void setFieldValue(String fieldValue) { this.fieldValue = fieldValue; }
    
}
