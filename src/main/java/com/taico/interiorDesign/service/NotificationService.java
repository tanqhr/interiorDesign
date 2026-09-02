package com.taico.interiorDesign.service;

import com.taico.interiorDesign.model.entity.NotificationEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;

import java.util.List;

    public interface NotificationService {

        void createNotification(
                UserEntity user,
                ProjectEntity project,
                String message
        );

        List<NotificationEntity> getUserNotifications(
                UserEntity user
        );

        long getUnreadCount(UserEntity user);

        NotificationEntity markAsRead(
                Long notificationId,
                UserEntity user
        );
    }

