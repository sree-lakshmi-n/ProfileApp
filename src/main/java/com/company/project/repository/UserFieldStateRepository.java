// UserFieldStateRepository.java
package com.company.project.repository;

import com.company.project.model.UserFieldState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFieldStateRepository extends JpaRepository<UserFieldState, Long> {
    
    Optional<UserFieldState> findByUserIdAndFieldDefId(Long userId, Long fieldDefId);
    
    List<UserFieldState> findByUserId(Long userId);
    
    List<UserFieldState> findByUserIdAndHasChanges(Long userId, Boolean hasChanges);
    
    void deleteByUserIdAndFieldDefId(Long userId, Long fieldDefId);
    
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    void deleteByFieldDefId(Long fieldDefId);
}