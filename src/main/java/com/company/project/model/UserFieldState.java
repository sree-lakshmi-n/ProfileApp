// UserFieldState.java
package com.company.project.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_field_state")
public class UserFieldState {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "field_def_id", nullable = false)
    private Long fieldDefId;
    
    @Column(name = "last_seen_version", nullable = false)
    private Integer lastSeenVersion = 1;
    
    @Column(name = "last_checked_at")
    private LocalDateTime lastCheckedAt;
    
    @Column(name = "has_changes", nullable = false)
    private Boolean hasChanges = false;
    
    // Constructors
    public UserFieldState() {
        this.lastCheckedAt = LocalDateTime.now();
    }
    
    public UserFieldState(Long userId, Long fieldDefId, Integer lastSeenVersion) {
        this.userId = userId;
        this.fieldDefId = fieldDefId;
        this.lastSeenVersion = lastSeenVersion;
        this.lastCheckedAt = LocalDateTime.now();
        this.hasChanges = false;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public Long getFieldDefId() { return fieldDefId; }
    public void setFieldDefId(Long fieldDefId) { this.fieldDefId = fieldDefId; }
    
    public Integer getLastSeenVersion() { return lastSeenVersion; }
    public void setLastSeenVersion(Integer lastSeenVersion) { this.lastSeenVersion = lastSeenVersion; }
    
    public LocalDateTime getLastCheckedAt() { return lastCheckedAt; }
    public void setLastCheckedAt(LocalDateTime lastCheckedAt) { this.lastCheckedAt = lastCheckedAt; }
    
    public Boolean getHasChanges() { return hasChanges; }
    public void setHasChanges(Boolean hasChanges) { this.hasChanges = hasChanges; }
}