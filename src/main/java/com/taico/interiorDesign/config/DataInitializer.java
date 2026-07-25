package com.taico.interiorDesign.config;


import com.taico.interiorDesign.enums.Role;
import com.taico.interiorDesign.model.entity.RoleEntity;
import com.taico.interiorDesign.model.entity.UserEntity;
import com.taico.interiorDesign.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.taico.interiorDesign.repositories.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RoleRepository roleRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        // Създаване на ролите
        for (Role role : Role.values()) {
            roleRepository.findByRole(role)
                    .orElseGet(() -> roleRepository.save(new RoleEntity(role)));
        }

        // Създаване на администратора
        if (userRepository.findByEmail("admin@taico.bg").isEmpty()) {

            RoleEntity adminRole = roleRepository.findByRole(Role.ADMIN)
                    .orElseThrow(() -> new IllegalStateException("ADMIN role not found"));

            UserEntity admin = new UserEntity();

            admin.setFirstName("Admin");
            admin.setLastName("TA&CO");
            admin.setUsername("admin");
            admin.setEmail("admin@taico.bg");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setActive(true);

            admin.addRoles(adminRole);

            userRepository.save(admin);
        }
    }
}