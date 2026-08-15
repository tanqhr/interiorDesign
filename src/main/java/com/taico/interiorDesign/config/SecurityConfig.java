package com.taico.interiorDesign.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**")
                )
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.POST, "/contact")
                        .permitAll()

                        .requestMatchers(
                                "/",
                                "/users/register",
                                "/users/login",
                                "/services",
                                "/prices",
                                "/about",
                                "/contact",
                                "/gallery",
                                "/faq",
                                "/api/faqs",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()


                                .requestMatchers(HttpMethod.GET, "/api/faqs")
                                .permitAll()


                                .requestMatchers(HttpMethod.GET, "/api/faqs/all")
                                .hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.POST, "/api/faqs")
                                .hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.PUT, "/api/faqs/**")
                                .hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.PATCH, "/api/faqs/**")
                                .hasAuthority("ADMIN")

                                .requestMatchers(HttpMethod.DELETE, "/api/faqs/**")
                                .hasAuthority("ADMIN")


                                .requestMatchers("/admin/**")
                                .hasAuthority("ADMIN")

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

                            boolean isAdmin =
                                    authentication.getAuthorities()
                                            .stream()
                                            .anyMatch(a ->
                                                    a.getAuthority()
                                                            .equals("ADMIN")
                                            );

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