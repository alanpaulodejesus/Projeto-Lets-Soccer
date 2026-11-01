package com.letssoccer.letssoccer.config;

import com.letssoccer.letssoccer.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@Profile("!test")
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )                // 401 quando não há autenticação
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )
                .authorizeHttpRequests(auth -> auth

                        // PUBLICOS
                        .requestMatchers(HttpMethod.POST, "/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios/cadastro").permitAll()

                        // USER
                        //.requestMatchers("/usuarios/clube").hasRole("USER")
                        //.requestMatchers("/usuarios/esquema").hasRole("USER")

                        //ADMIN E USER
                        .requestMatchers(HttpMethod.POST, "/usuarios/clube")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/clube/{id}/esquema-tatico")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.GET, "/clube/{id}/escalacao")
                        .hasAnyRole("USER", "ADMIN")
                        // ADMIN
                        //.requestMatchers("/clubes/**").hasRole("ADMIN")

                        //.anyRequest().authenticated()
                        .anyRequest().permitAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}