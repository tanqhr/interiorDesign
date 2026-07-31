package com.taico.interiorDesign.service.impl;

import com.taico.interiorDesign.enums.Role;
import com.taico.interiorDesign.model.dto.UserRegisterDTO;
import com.taico.interiorDesign.model.entity.RoleEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.security.CurrentUser;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.taico.interiorDesign.repositories.RoleRepository;
import com.taico.interiorDesign.repositories.UserRepository;
import com.taico.interiorDesign.service.UserService;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionInformation;





import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final SessionRegistry sessionRegistry;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder, SessionRegistry sessionRegistry) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void register(UserRegisterDTO dto) {

        // 1. Проверка за съвпадение на паролите
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("Паролите не съвпадат.");
        }

        // 2. Проверка за съществуващ username
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new IllegalArgumentException("Потребителското име вече съществува.");
        }

        // 3. Проверка за съществуващ email
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Имейлът вече е регистриран.");
        }

        // 4. Намираме ролята USER
        RoleEntity userRole = roleRepository.findByRole(Role.USER)
                .orElseThrow(() -> new IllegalStateException("Липсва роля USER"));

        // 5. Създаваме Entity
        UserEntity user = new UserEntity();

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        // криптиране на паролата
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setActive(true);

        user.addRoles(userRole);

        // 6. Запис
        userRepository.save(user);
    }


    public boolean existsByEmail (String email) {

        return this.userRepository.existsByEmail(email);
    }
    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {


        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));


        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities(
                        user.getRoles()
                                .stream()
                                .map(role -> role.getRole().name())
                                .toArray(String[]::new)
                )
                .build();
    }

    @Override
    @Transactional
    public void deactivateUser(Long userId) {

        UserEntity user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Потребителят не е намерен."
                                )
                        );

        user.setActive(false);

        userRepository.save(user);


        List<Object> principals =
                sessionRegistry.getAllPrincipals();


        // 3. Намираме конкретния потребител
        for (Object principal : principals) {

            if (principal instanceof CurrentUser currentUser) {

                if (currentUser.getId().equals(userId)) {

                    // 4. Намираме активните му сесии
                    List<SessionInformation> sessions =
                            sessionRegistry.getAllSessions(
                                    principal,
                                    false
                            );

                    // 5. Прекратяваме ги
                    for (SessionInformation session : sessions) {

                        session.expireNow();
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void activateUser(Long userId) {

        UserEntity user =
                userRepository.findById(userId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Потребителят не е намерен."
                                )
                        );

        user.setActive(true);

        userRepository.save(user);
    }

    @Override
    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

}
