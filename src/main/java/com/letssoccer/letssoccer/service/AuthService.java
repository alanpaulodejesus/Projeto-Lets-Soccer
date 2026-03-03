package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.dto.LoginRequestDto;
import com.letssoccer.letssoccer.messages.exception.UnauthorizedException;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public String login(LoginRequestDto dto) {
        try {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.email(),
                        dto.senha()
                )
        );

        return jwtService.gerarToken(
                (UserDetails) auth.getPrincipal()
            );
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Email ou senha inválidos");
        }
    }
}