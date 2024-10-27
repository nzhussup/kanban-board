package com.nzhussup.kanbanservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeHttpRequests(requests -> {
                    requests.requestMatchers("/auth/login").permitAll();
                    requests.requestMatchers("/auth/register").permitAll();
                    requests.requestMatchers("/auth/admin/**").hasRole("ADMIN");
                    requests.requestMatchers("/api/admin/**").hasRole("ADMIN");
                    requests.requestMatchers("/api/board/admin/**").hasRole("ADMIN");
                    requests.requestMatchers("/api/list/admin/**").hasRole("ADMIN");
                    requests.requestMatchers("/api/card/admin/**").hasRole("ADMIN");
                    requests.anyRequest().authenticated();
                })
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(new BCryptPasswordEncoder(12));
        return authProvider;
    }
}
