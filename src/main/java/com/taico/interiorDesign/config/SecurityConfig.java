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
                                    "/services",
                                    "/css/**",
                                    "/js/**",
                                    "/images/**",
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
                            .successHandler((request, response, authentication) -> {

                                boolean isAdmin = authentication.getAuthorities().stream()
                                        .anyMatch(a -> a.getAuthority().equals("ADMIN"));

                                if (isAdmin) {
                                    response.sendRedirect("/admin");
                                } else {
                                    response.sendRedirect("/home");
                                }
                            })
                            .permitAll()
                    )

                    .logout(logout -> logout
                            .logoutSuccessUrl("/")
                            .permitAll()
                    );

            return httpSecurity.build();
        }
    }

