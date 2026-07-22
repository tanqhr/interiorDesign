package com.taico.interiorDesign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
public class SecurityConfig {


    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();

    }



        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

            httpSecurity
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers(
                                    "/",
                                    "/users/register",
                                    "/users/login",
                                    "/style.css",
                                    "/register.css",
                                    "/login.css",
                                    "/js/**",
                                    "/images/**"
                            ).permitAll()

                            .requestMatchers("/admin/**")
                            .hasAuthority("ADMIN")

                            .anyRequest()
                            .authenticated()
                    )

                    .formLogin(login -> login
                            .loginPage("/users/login")
                            .usernameParameter("email")
                            .passwordParameter("password")
                            .defaultSuccessUrl("/home", true)
                            .permitAll()
                    )

                    .logout(logout -> logout
                            .logoutSuccessUrl("/")
                            .permitAll()
                    );

            return httpSecurity.build();
        }
    }

