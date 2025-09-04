package com.zoedatalab.notesbackend.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@ConditionalOnProperty(name = "app.security.enabled", havingValue = "true")
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/health", "/actuator/health/**", "/actuator/info", "/actuator/**",
                                "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return http.build();
    }
}
