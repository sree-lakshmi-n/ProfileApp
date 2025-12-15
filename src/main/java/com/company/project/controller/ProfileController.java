package com.company.project.controller;

import com.company.project.model.UserProfileValue;
import com.company.project.repository.UserProfileValueRepository;
import com.company.project.repository.ProfileFieldDefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
public class ProfileController {

    @Autowired
    private UserProfileValueRepository userProfileValueRepository;

    @Autowired
    private ProfileFieldDefRepository profileFieldDefRepository;

    @PostMapping("/profile/save")
    @Transactional
    public String saveProfile(@RequestParam Map<String, String> params,
                              @RequestParam("redirectTo") String redirectTo,
                              RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // current authenticated user is a com.company.project.model.User (UserDetails)
        com.company.project.model.User currentUser = (com.company.project.model.User) auth.getPrincipal();
        Long userId = currentUser.getId();

        params.forEach((key, value) -> {
            if (key.startsWith("field_")) {
                // key format: field_{fieldId}
                String idStr = key.substring("field_".length());
                try {
                    Long fieldDefId = Long.valueOf(idStr);

                    // Normalize checkbox (BOOLEAN) empty value to "false"
                    String normalizedValue = value;
                    if (normalizedValue == null || normalizedValue.isBlank()) {
                        normalizedValue = "";
                    }

                    var existing = userProfileValueRepository.findByUserIdAndFieldDefId(userId, fieldDefId);
                    if (existing.isPresent()) {
                        var upv = existing.get();
                        upv.setFieldValue(normalizedValue);
                        userProfileValueRepository.save(upv);
                    } else {
                        var upv = new UserProfileValue(userId, fieldDefId, normalizedValue);
                        userProfileValueRepository.save(upv);
                    }
                } catch (NumberFormatException ignored) {
                    // skip non-id params
                }
            }
        });

        redirectAttributes.addFlashAttribute("success", "Profile saved successfully.");
        return "redirect:" + redirectTo;
    }
}