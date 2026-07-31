package com.taico.interiorDesign.security;


import com.taico.interiorDesign.model.entity.UserEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

@Getter
@Setter
@Accessors(chain = true)
public class CurrentUser extends User {


    private Long id;

    private String firstName;

    private String lastName;


    public CurrentUser(
            String username,
            String password,
            boolean enabled,
            Collection<? extends GrantedAuthority> authorities) {

        super(
                username,
                password,
                true,       // accountNonExpired
                true,       // credentialsNonExpired
                true,       // accountNonLocked
                enabled,    // enabled
                authorities
        );
    }



    public static CurrentUser fromEntity(UserEntity user) {


        return new CurrentUser(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                user.getRoles()
                        .stream()
                        .map(role ->
                                new SimpleGrantedAuthority(
                                        role.getRole().name()
                                ))
                        .toList())

                .setId(user.getId())
                .setFirstName(user.getFirstName())
                .setLastName(user.getLastName());
    }

}