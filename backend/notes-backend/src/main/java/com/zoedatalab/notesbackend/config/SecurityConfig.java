package com.zoedatalab.notesbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableConfigurationProperties(CorsProps.class) // 👈 activa el binding type-safe
public class SecurityConfig {

    private final boolean securityEnabled;
    private final CorsProps corsProps;

    public SecurityConfig(
            @Value("${app.security.enabled:false}") boolean securityEnabled,
            CorsProps corsProps
    ) {
        this.securityEnabled = securityEnabled;
        this.corsProps = corsProps;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults());

        if (securityEnabled) {
            http.authorizeHttpRequests(auth -> auth
                    .requestMatchers(
                            "/actuator/health", "/actuator/health/**", "/actuator/info", "/actuator/**",
                            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
                    ).permitAll()
                    .anyRequest().authenticated()
            ).httpBasic(Customizer.withDefaults());
        } else {
            http.authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        }

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> patterns = corsProps.getAllowedOrigins().isEmpty()
                ? List.of("http://localhost:*", "http://127.0.0.1:*") // fallback dev
                : corsProps.getAllowedOrigins();

        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOriginPatterns(patterns);
        cfg.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS","PATCH"));
        cfg.setAllowedHeaders(List.of("*"));
        cfg.setExposedHeaders(List.of("Location"));
        cfg.setAllowCredentials(false); // en dev sin cookies

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }
}
