package com.taico.interiorDesign.web;

import com.taico.interiorDesign.model.entity.NotificationEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.UserRepository;
import com.taico.interiorDesign.security.CurrentUser;
import com.taico.interiorDesign.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;


    @GetMapping
    public String getNotifications(
            Authentication authentication,
            Model model) {

        CurrentUser currentUser =
                (CurrentUser) authentication.getPrincipal();

        UserEntity user =
                userRepository.findById(currentUser.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Потребителят не е намерен."
                                )
                        );

        List<NotificationEntity> notifications =
                notificationService.getUserNotifications(user);

        model.addAttribute(
                "notifications",
                notifications
        );

        model.addAttribute(
                "unreadCount",
                notificationService.getUnreadCount(user)
        );

        return "notifications";
    }


    @PostMapping("/{id}/read")
    public String markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        CurrentUser currentUser =
                (CurrentUser) authentication.getPrincipal();

        UserEntity user =
                userRepository.findById(currentUser.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Потребителят не е намерен."
                                )
                        );

        notificationService.markAsRead(
                id,
                user
        );

        return "redirect:/notifications";
    }

    @GetMapping("/{id}")
    public String openNotification(
            @PathVariable Long id,
            Authentication authentication) {

        CurrentUser currentUser =
                (CurrentUser) authentication.getPrincipal();

        UserEntity user =
                userRepository.findById(currentUser.getId())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Потребителят не е намерен."
                                )
                        );

        NotificationEntity notification =
                notificationService.markAsRead(
                        id,
                        user
                );

        return "redirect:/projects/"
                + notification.getProject().getId();
    }
}
