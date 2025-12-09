// ProfileFieldDefRepository.java
package com.company.project.repository;

import com.company.project.model.ProfileFieldDef;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileFieldDefRepository extends JpaRepository<ProfileFieldDef, Long> {

    List<ProfileFieldDef> findByTargetRole(String string);
}