package com.letssoccer.letssoccer.unit;

import com.letssoccer.letssoccer.dto.LoginRequestDto;
import com.letssoccer.letssoccer.messages.exception.UnauthorizedException;
import com.letssoccer.letssoccer.service.AuthService;
import com.letssoccer.letssoccer.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveRealizarLoginComSucesso() {

        LoginRequestDto dto = new LoginRequestDto(
                "user@email.com",
                "123"
        );

        when(authenticationManager.authenticate(any()))
                .thenReturn(authentication);

        when(authentication.getPrincipal())
                .thenReturn(userDetails);

        when(jwtService.gerarToken(userDetails))
                .thenReturn("tokenGerado");

        String token = service.login(dto);

        assertEquals("tokenGerado", token);

        verify(authenticationManager).authenticate(any());
        verify(jwtService).gerarToken(userDetails);
    }

    @Test
    void deveLancarUnauthorizedQuandoCredenciaisInvalidas() {

        LoginRequestDto dto = new LoginRequestDto(
                "user@email.com",
                "senhaErrada"
        );

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Credenciais inválidas"));

        UnauthorizedException ex = assertThrows(
                UnauthorizedException.class,
                () -> service.login(dto)
        );

        assertEquals("Email ou senha inválidos", ex.getMessage());

        verify(jwtService, never()).gerarToken(any());
    }
}
