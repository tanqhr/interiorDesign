package com.taico.interiorDesign.web;

import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.UserRepository;
import com.taico.interiorDesign.security.CurrentUser;
import com.taico.interiorDesign.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalModelAttributes {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @ModelAttribute("unreadCount")
    public long unreadCount(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof CurrentUser)) {

            return 0;
        }

        CurrentUser currentUser =
                (CurrentUser) authentication.getPrincipal();

        UserEntity user =
                userRepository.findById(currentUser.getId())
                        .orElse(null);

        if (user == null) {
            return 0;
        }

        return notificationService.getUnreadCount(user);
    }
}