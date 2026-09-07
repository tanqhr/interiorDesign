package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.model.entity.NotificationEntity;
import com.taico.interiorDesign.model.entity.ProjectEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.NotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceImplTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationServiceImpl notificationService;


    @Test
    void createNotification_ShouldSaveNotification() {

        UserEntity user = new UserEntity();
        ProjectEntity project = new ProjectEntity();

        String message = "Новият дизайн е готов.";

        notificationService.createNotification(
                user,
                project,
                message
        );

        ArgumentCaptor<NotificationEntity> captor =
                ArgumentCaptor.forClass(NotificationEntity.class);

        verify(notificationRepository)
                .save(captor.capture());

        NotificationEntity savedNotification =
                captor.getValue();

        assertEquals(
                user,
                savedNotification.getUser()
        );

        assertEquals(
                project,
                savedNotification.getProject()
        );

        assertEquals(
                message,
                savedNotification.getMessage()
        );

        assertFalse(
                savedNotification.isRead()
        );
    }



    @Test
    void getUserNotifications_ShouldReturnUserNotifications() {

        UserEntity user = new UserEntity();

        NotificationEntity notification1 =
                new NotificationEntity();

        NotificationEntity notification2 =
                new NotificationEntity();

        List<NotificationEntity> notifications =
                List.of(notification1, notification2);

        when(notificationRepository
                .findByUserOrderByCreatedAtDesc(user))
                .thenReturn(notifications);

        List<NotificationEntity> result =
                notificationService.getUserNotifications(user);

        assertEquals(
                notifications,
                result
        );

        verify(notificationRepository)
                .findByUserOrderByCreatedAtDesc(user);
    }


    @Test
    void getUnreadCount_ShouldReturnUnreadCount() {

        UserEntity user = new UserEntity();

        when(notificationRepository
                .countByUserAndReadFalse(user))
                .thenReturn(5L);

        long result =
                notificationService.getUnreadCount(user);

        assertEquals(
                5L,
                result
        );

        verify(notificationRepository)
                .countByUserAndReadFalse(user);
    }


    @Test
    void markAsRead_WhenNotificationBelongsToUser_ShouldMarkAsRead() {

        UserEntity user = new UserEntity();
        user.setId(1L);

        NotificationEntity notification =
                new NotificationEntity();

        notification.setUser(user);
        notification.setRead(false);

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        when(notificationRepository.save(notification))
                .thenReturn(notification);

        NotificationEntity result =
                notificationService.markAsRead(
                        1L,
                        user
                );

        assertTrue(
                result.isRead()
        );

        assertTrue(
                notification.isRead()
        );

        verify(notificationRepository)
                .findById(1L);

        verify(notificationRepository)
                .save(notification);
    }


    @Test
    void markAsRead_WhenNotificationDoesNotExist_ShouldThrowException() {

        UserEntity user = new UserEntity();
        user.setId(1L);

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> notificationService.markAsRead(
                        1L,
                        user
                )
        );

        verify(notificationRepository)
                .findById(1L);

        verify(notificationRepository, never())
                .save(any(NotificationEntity.class));
    }


    @Test
    void markAsRead_WhenNotificationBelongsToAnotherUser_ShouldThrowException() {

        UserEntity notificationOwner = new UserEntity();
        notificationOwner.setId(1L);

        UserEntity currentUser = new UserEntity();
        currentUser.setId(2L);

        NotificationEntity notification =
                new NotificationEntity();

        notification.setUser(notificationOwner);
        notification.setRead(false);

        when(notificationRepository.findById(1L))
                .thenReturn(Optional.of(notification));

        assertThrows(
                RuntimeException.class,
                () -> notificationService.markAsRead(
                        1L,
                        currentUser
                )
        );

        assertFalse(
                notification.isRead()
        );

        verify(notificationRepository)
                .findById(1L);

        verify(notificationRepository, never())
                .save(any(NotificationEntity.class));
    }

}
