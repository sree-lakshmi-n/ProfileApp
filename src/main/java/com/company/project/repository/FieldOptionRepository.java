package com.company.project.repository;

import com.company.project.model.FieldOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface FieldOptionRepository extends JpaRepository<FieldOption, Long> {
    
    // Find all options for a specific field definition
    List<FieldOption> findByFieldDefId(Long fieldDefId);
    
    // Count options for a field definition
    long countByFieldDefId(Long fieldDefId);
    
    // Delete all options for a specific field definition
    @Transactional
    @Modifying
    @Query("DELETE FROM FieldOption fo WHERE fo.fieldDefId = :fieldDefId")
    void deleteByFieldDefId(@Param("fieldDefId") Long fieldDefId);
    
    // Check if a field definition has any options
    boolean existsByFieldDefId(Long fieldDefId);
}