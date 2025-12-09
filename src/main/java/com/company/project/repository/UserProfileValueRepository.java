package com.company.project.repository;

import com.company.project.model.UserProfileValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserProfileValueRepository extends JpaRepository<UserProfileValue, Long> {
    
    // Count how many user values exist for a specific field definition
    long countByFieldDefId(Long fieldDefId);
    
    // Find all user values for a specific field definition
    List<UserProfileValue> findByFieldDefId(Long fieldDefId);
    
    // Find a user's value for a specific field
    Optional<UserProfileValue> findByUserIdAndFieldDefId(Long userId, Long fieldDefId);
    
    // Find all values for a specific user
    List<UserProfileValue> findByUserId(Long userId);
    
    // Delete all user values for a specific field definition
    @Transactional
    @Modifying
    @Query("DELETE FROM UserProfileValue upv WHERE upv.fieldDefId = :fieldDefId")
    void deleteByFieldDefId(@Param("fieldDefId") Long fieldDefId);
    
    // Delete a specific user's value for a field
    @Transactional
    @Modifying
    void deleteByUserIdAndFieldDefId(Long userId, Long fieldDefId);
    
    // Check if a user has a value for a specific field
    boolean existsByUserIdAndFieldDefId(Long userId, Long fieldDefId);
}