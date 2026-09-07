package com.taico.interiorDesign.service.impl;


import com.taico.interiorDesign.enums.Role;
import com.taico.interiorDesign.model.dto.UserRegisterDTO;
import com.taico.interiorDesign.model.entity.RoleEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.RoleRepository;
import com.taico.interiorDesign.repositories.UserRepository;
import com.taico.interiorDesign.security.CurrentUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SessionRegistry sessionRegistry;

    @InjectMocks
    private UserServiceImpl userService;


    // =========================================================
    // REGISTER
    // =========================================================

    @Test
    void register_shouldRegisterUserSuccessfully() {

        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();

        dto.setFirstName("Ivan");
        dto.setLastName("Ivanov");
        dto.setUsername("ivan123");
        dto.setEmail("ivan@gmail.com");
        dto.setPassword("password");
        dto.setConfirmPassword("password");

        RoleEntity userRole = mock(RoleEntity.class);

        when(userRepository.existsByUsername("ivan123"))
                .thenReturn(false);

        when(userRepository.existsByEmail("ivan@gmail.com"))
                .thenReturn(false);

        when(roleRepository.findByRole(Role.USER))
                .thenReturn(Optional.of(userRole));

        when(passwordEncoder.encode("password"))
                .thenReturn("encodedPassword");

        // Act
        userService.register(dto);

        // Assert
        ArgumentCaptor<UserEntity> captor =
                ArgumentCaptor.forClass(UserEntity.class);

        verify(userRepository).save(captor.capture());

        UserEntity savedUser = captor.getValue();

        assertEquals("Ivan", savedUser.getFirstName());
        assertEquals("Ivanov", savedUser.getLastName());
        assertEquals("ivan123", savedUser.getUsername());
        assertEquals("ivan@gmail.com", savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());

        assertTrue(savedUser.isActive());

        verify(passwordEncoder).encode("password");
        verify(roleRepository).findByRole(Role.USER);
    }


    @Test
    void register_shouldThrowException_whenPasswordsDoNotMatch() {

        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();

        dto.setUsername("ivan123");
        dto.setEmail("ivan@gmail.com");
        dto.setPassword("password");
        dto.setConfirmPassword("differentPassword");

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.register(dto)
                );

        // Assert
        assertEquals(
                "Паролите не съвпадат.",
                exception.getMessage()
        );

        verifyNoInteractions(userRepository);
        verifyNoInteractions(roleRepository);
        verifyNoInteractions(passwordEncoder);
    }


    @Test
    void register_shouldThrowException_whenUsernameAlreadyExists() {

        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();

        dto.setUsername("ivan123");
        dto.setEmail("ivan@gmail.com");
        dto.setPassword("password");
        dto.setConfirmPassword("password");

        when(userRepository.existsByUsername("ivan123"))
                .thenReturn(true);

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.register(dto)
                );

        // Assert
        assertEquals(
                "Потребителското име вече съществува.",
                exception.getMessage()
        );

        verify(userRepository).existsByUsername("ivan123");

        verify(userRepository, never())
                .existsByEmail(anyString());

        verifyNoInteractions(roleRepository);
        verifyNoInteractions(passwordEncoder);
    }


    @Test
    void register_shouldThrowException_whenEmailAlreadyExists() {

        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();

        dto.setUsername("ivan123");
        dto.setEmail("ivan@gmail.com");
        dto.setPassword("password");
        dto.setConfirmPassword("password");

        when(userRepository.existsByUsername("ivan123"))
                .thenReturn(false);

        when(userRepository.existsByEmail("ivan@gmail.com"))
                .thenReturn(true);

        // Act
        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> userService.register(dto)
                );

        // Assert
        assertEquals(
                "Имейлът вече е регистриран.",
                exception.getMessage()
        );

        verify(userRepository).existsByUsername("ivan123");
        verify(userRepository).existsByEmail("ivan@gmail.com");

        verifyNoInteractions(roleRepository);
        verifyNoInteractions(passwordEncoder);
    }


    @Test
    void register_shouldThrowException_whenUserRoleDoesNotExist() {

        // Arrange
        UserRegisterDTO dto = new UserRegisterDTO();

        dto.setUsername("ivan123");
        dto.setEmail("ivan@gmail.com");
        dto.setPassword("password");
        dto.setConfirmPassword("password");

        when(userRepository.existsByUsername("ivan123"))
                .thenReturn(false);

        when(userRepository.existsByEmail("ivan@gmail.com"))
                .thenReturn(false);

        when(roleRepository.findByRole(Role.USER))
                .thenReturn(Optional.empty());

        // Act
        IllegalStateException exception =
                assertThrows(
                        IllegalStateException.class,
                        () -> userService.register(dto)
                );

        // Assert
        assertEquals(
                "Липсва роля USER",
                exception.getMessage()
        );

        verify(roleRepository).findByRole(Role.USER);

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }


    // =========================================================
    // EXISTS BY EMAIL
    // =========================================================

    @Test
    void existsByEmail_shouldReturnTrue_whenEmailExists() {

        // Arrange
        when(userRepository.existsByEmail("ivan@gmail.com"))
                .thenReturn(true);

        // Act
        boolean result =
                userService.existsByEmail("ivan@gmail.com");

        // Assert
        assertTrue(result);

        verify(userRepository)
                .existsByEmail("ivan@gmail.com");
    }


    @Test
    void existsByEmail_shouldReturnFalse_whenEmailDoesNotExist() {

        // Arrange
        when(userRepository.existsByEmail("ivan@gmail.com"))
                .thenReturn(false);

        // Act
        boolean result =
                userService.existsByEmail("ivan@gmail.com");

        // Assert
        assertFalse(result);

        verify(userRepository)
                .existsByEmail("ivan@gmail.com");
    }


    // =========================================================
    // LOAD USER BY USERNAME
    // =========================================================

    @Test
    void loadUserByUsername_shouldReturnUserDetails() {

        // Arrange
        UserEntity user = mock(UserEntity.class);
        RoleEntity roleEntity = mock(RoleEntity.class);

        when(user.getEmail())
                .thenReturn("ivan@gmail.com");

        when(user.getPassword())
                .thenReturn("encodedPassword");

        when(user.getRoles())
                .thenReturn(List.of(roleEntity));

        when(roleEntity.getRole())
                .thenReturn(Role.USER);

        when(userRepository.findByEmail("ivan@gmail.com"))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails result =
                userService.loadUserByUsername("ivan@gmail.com");

        // Assert
        assertNotNull(result);

        assertEquals(
                "ivan@gmail.com",
                result.getUsername()
        );

        assertEquals(
                "encodedPassword",
                result.getPassword()
        );

        assertTrue(
                result.getAuthorities()
                        .stream()
                        .anyMatch(
                                authority ->
                                        authority
                                                .getAuthority()
                                                .equals("USER")
                        )
        );

        verify(userRepository)
                .findByEmail("ivan@gmail.com");
    }


    @Test
    void loadUserByUsername_shouldThrowException_whenUserDoesNotExist() {

        // Arrange
        when(userRepository.findByEmail("unknown@gmail.com"))
                .thenReturn(Optional.empty());

        // Act
        UsernameNotFoundException exception =
                assertThrows(
                        UsernameNotFoundException.class,
                        () -> userService.loadUserByUsername(
                                "unknown@gmail.com"
                        )
                );

        // Assert
        assertEquals(
                "User not found",
                exception.getMessage()
        );

        verify(userRepository)
                .findByEmail("unknown@gmail.com");
    }


    // =========================================================
    // DEACTIVATE USER
    // =========================================================

    @Test
    void deactivateUser_shouldDeactivateUserSuccessfully() {

        // Arrange
        Long userId = 1L;

        UserEntity user = mock(UserEntity.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(sessionRegistry.getAllPrincipals())
                .thenReturn(List.of());

        // Act
        userService.deactivateUser(userId);

        // Assert
        verify(user).setActive(false);

        verify(userRepository)
                .save(user);

        verify(sessionRegistry)
                .getAllPrincipals();
    }


    @Test
    void deactivateUser_shouldExpireUserSessions() {

        // Arrange
        Long userId = 1L;

        UserEntity user = mock(UserEntity.class);

        CurrentUser currentUser = mock(CurrentUser.class);

        SessionInformation session1 =
                mock(SessionInformation.class);

        SessionInformation session2 =
                mock(SessionInformation.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(sessionRegistry.getAllPrincipals())
                .thenReturn(List.of(currentUser));

        when(currentUser.getId())
                .thenReturn(userId);

        when(sessionRegistry.getAllSessions(
                currentUser,
                false
        )).thenReturn(List.of(session1, session2));

        // Act
        userService.deactivateUser(userId);

        // Assert
        verify(user).setActive(false);

        verify(userRepository)
                .save(user);

        verify(sessionRegistry)
                .getAllSessions(currentUser, false);

        verify(session1)
                .expireNow();

        verify(session2)
                .expireNow();
    }


    @Test
    void deactivateUser_shouldNotExpireSessions_whenPrincipalBelongsToAnotherUser() {

        // Arrange
        Long userId = 1L;

        UserEntity user = mock(UserEntity.class);

        CurrentUser anotherUser =
                mock(CurrentUser.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(sessionRegistry.getAllPrincipals())
                .thenReturn(List.of(anotherUser));

        when(anotherUser.getId())
                .thenReturn(2L);

        // Act
        userService.deactivateUser(userId);

        // Assert
        verify(user).setActive(false);
        verify(userRepository).save(user);

        verify(sessionRegistry, never())
                .getAllSessions(any(), anyBoolean());
    }


    @Test
    void deactivateUser_shouldThrowException_whenUserDoesNotExist() {

        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.deactivateUser(userId)
                );

        // Assert
        assertEquals(
                "Потребителят не е намерен.",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository, never())
                .save(any(UserEntity.class));

        verifyNoInteractions(sessionRegistry);
    }


    // =========================================================
    // ACTIVATE USER
    // =========================================================

    @Test
    void activateUser_shouldActivateUserSuccessfully() {

        // Arrange
        Long userId = 1L;

        UserEntity user = mock(UserEntity.class);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        // Act
        userService.activateUser(userId);

        // Assert
        verify(user).setActive(true);

        verify(userRepository)
                .save(user);
    }


    @Test
    void activateUser_shouldThrowException_whenUserDoesNotExist() {

        // Arrange
        Long userId = 1L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        // Act
        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> userService.activateUser(userId)
                );

        // Assert
        assertEquals(
                "Потребителят не е намерен.",
                exception.getMessage()
        );

        verify(userRepository)
                .findById(userId);

        verify(userRepository, never())
                .save(any(UserEntity.class));
    }


    // =========================================================
    // FIND ALL
    // =========================================================

    @Test
    void findAll_shouldReturnAllUsers() {

        // Arrange
        UserEntity user1 = mock(UserEntity.class);
        UserEntity user2 = mock(UserEntity.class);

        List<UserEntity> users =
                List.of(user1, user2);

        when(userRepository.findAll())
                .thenReturn(users);

        // Act
        List<UserEntity> result =
                userService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(users, result);

        verify(userRepository).findAll();
    }


    @Test
    void findAll_shouldReturnEmptyList_whenThereAreNoUsers() {

        // Arrange
        when(userRepository.findAll())
                .thenReturn(List.of());

        // Act
        List<UserEntity> result =
                userService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(userRepository).findAll();
    }
}


