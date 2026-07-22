package com.taico.interiorDesign.services;


import com.taico.interiorDesign.model.dto.UserRegisterDTO;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService {

    void register(UserRegisterDTO dto);

    boolean existsByEmail (String email);

UserDetails loadUserByUsername(String email) throws UsernameNotFoundException;


}
