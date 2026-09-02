package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.model.entity.NotificationEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.NotificationRepository;
import com.taico.interiorDesign.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public void createNotification(
            UserEntity user,
            ProjectEntity project,
            String message
    ) {
        NotificationEntity notification = new NotificationEntity();

        notification.setUser(user);
        notification.setProject(project);
        notification.setMessage(message);
        notification.setRead(false);

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationEntity> getUserNotifications(
            UserEntity user
    ) {
        return notificationRepository
                .findByUserOrderByCreatedAtDesc(user);
    }

    @Override
    public long getUnreadCount(UserEntity user) {
        return notificationRepository
                .countByUserAndReadFalse(user);
    }

    @Override
    public NotificationEntity markAsRead(
            Long notificationId,
            UserEntity user
    ) {

        NotificationEntity notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Notification not found"
                                )
                        );

        if (!notification.getUser().getId()
                .equals(user.getId())) {

            throw new RuntimeException(
                    "Нямате достъп до това известие."
            );
        }

        notification.setRead(true);

        return notificationRepository.save(notification);
    }
}
