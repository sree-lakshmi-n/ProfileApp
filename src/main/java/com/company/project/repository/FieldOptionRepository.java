package com.company.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.company.project.model.FieldOption;

public interface FieldOptionRepository extends JpaRepository<FieldOption, Long> {
    
}
