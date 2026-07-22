package com.taico.interiorDesign.services.impl;

import com.taico.interiorDesign.enums.Role;
import com.taico.interiorDesign.model.dto.UserRegisterDTO;
import com.taico.interiorDesign.model.entity.RoleEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.taico.interiorDesign.repositories.RoleRepository;
import com.taico.interiorDesign.repositories.UserRepository;
import com.taico.interiorDesign.services.UserService;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
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



}
