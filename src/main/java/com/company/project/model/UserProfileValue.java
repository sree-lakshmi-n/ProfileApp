// UserProfileValue.java
package com.company.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profile_value")
public class UserProfileValue {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "field_def_id", nullable = false)
    private Long fieldDefId;
    
    @Column(name = "field_value")
    private String fieldValue;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Constructors
    public UserProfileValue() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public UserProfileValue(Long userId, Long fieldDefId, String fieldValue) {
        this.userId = userId;
        this.fieldDefId = fieldDefId;
        this.fieldValue = fieldValue;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getFieldDefId() { return fieldDefId; }
    public void setFieldDefId(Long fieldDefId) { this.fieldDefId = fieldDefId; }
    
    public String getFieldValue() { return fieldValue; }
    public void setFieldValue(String fieldValue) { 
        this.fieldValue = fieldValue;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}