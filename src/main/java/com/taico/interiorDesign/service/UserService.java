package com.taico.interiorDesign.service;


import com.taico.interiorDesign.model.dto.UserRegisterDTO;
import com.taico.interiorDesign.model.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService {

    void register(UserRegisterDTO dto);

    boolean existsByEmail (String email);

    UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;

    void deactivateUser(Long userId);

    void activateUser(Long userId);

    List<UserEntity> findAll();


}
