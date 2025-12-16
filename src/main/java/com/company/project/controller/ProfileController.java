package com.company.project.controller;

import com.company.project.model.UserProfileValue;
import com.company.project.repository.UserProfileValueRepository;
import com.company.project.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;
import java.util.Optional;

@Controller
public class ProfileController {

    @Autowired
    private UserProfileValueRepository userProfileValueRepository;

    @PostMapping("/profile/save")
    @Transactional
    public String saveProfile(@RequestParam Map<String, String> params,
                              @RequestParam("redirectTo") String redirectTo,
                              RedirectAttributes redirectAttributes) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        Long userId = currentUser.getId();

        params.forEach((key, value) -> {
            if (key.startsWith("field_")) {
                String idStr = key.substring("field_".length());
                try {
                    Long fieldDefId = Long.valueOf(idStr);

                    String normalizedValue = value;
                    if (normalizedValue == null || normalizedValue.isBlank()) {
                        normalizedValue = "";
                    }

                    Optional<UserProfileValue> existing = userProfileValueRepository.findByUserIdAndFieldDefId(userId, fieldDefId);
                    if (existing.isPresent()) {
                        UserProfileValue upv = existing.get();
                        upv.setFieldValue(normalizedValue);
                        userProfileValueRepository.save(upv);
                    } else {
                        UserProfileValue upv = new UserProfileValue(userId, fieldDefId, normalizedValue);
                        userProfileValueRepository.save(upv);
                    }
                } catch (NumberFormatException ignored) {
                }
            }
        });

        redirectAttributes.addFlashAttribute("success", "Profile saved successfully.");
        return "redirect:" + redirectTo;
    }
}