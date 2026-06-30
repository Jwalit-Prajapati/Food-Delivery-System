package com.fooddelivery.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jakarta.servlet.DispatcherType;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            // Allow both STATELESS (JWT/API) and ALWAYS (Web UI/session) — use IF_REQUIRED
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                // Allow container forwards (JSP rendering) and error dispatches
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

                // ── Public API endpoints ──────────────────────────────────────
                .requestMatchers(
                    "/api/users/login",
                    "/api/users/register",
                    "/actuator/health",
                    "/swagger-ui/**",
                    "/v3/api-docs/**"
                ).permitAll()

                // ── Public Web UI pages ───────────────────────────────────────
                // Auth
                .requestMatchers("/", "/login", "/register", "/logout").permitAll()
                // Static resources
                .requestMatchers("/resources/**", "/static/**", "/webjars/**", "/error").permitAll()
                // CSS/JS/images under webapp
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()

                // ── Web UI pages (controller guards access internally via session) ──
                .requestMatchers(
                    "/home", "/home/**",
                    "/restaurants", "/restaurants/**",
                    "/cart", "/cart/**",
                    "/orders", "/orders/**",
                    "/profile", "/profile/**",
                    "/reviews/**",
                    "/owner/**",
                    "/admin/**",
                    "/delivery/**"
                ).permitAll()

                // ── All API endpoints require JWT authentication ──────────────
                .requestMatchers("/api/**").authenticated()

                // Anything else — permit (controllers handle auth)
                .anyRequest().permitAll()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
