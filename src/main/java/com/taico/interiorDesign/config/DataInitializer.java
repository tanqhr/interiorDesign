package com.taico.interiorDesign.config;


import com.taico.interiorDesign.enums.Role;
import com.taico.interiorDesign.model.entity.RoleEntity;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import com.taico.interiorDesign.repositories.RoleRepository;

@Component
public class DataInitializer implements CommandLineRunner {


    private final RoleRepository roleRepository;


    public DataInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {

        for (Role role : Role.values()) {

            roleRepository.findByRole(role)
                    .orElseGet(() -> roleRepository.save(new RoleEntity(role)));
        }
    }
}
