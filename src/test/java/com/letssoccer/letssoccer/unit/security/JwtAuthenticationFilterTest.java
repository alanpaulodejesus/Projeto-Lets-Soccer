package com.letssoccer.letssoccer.unit.security;

import com.letssoccer.letssoccer.security.JwtAuthenticationFilter;
import com.letssoccer.letssoccer.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private UserDetails usuario;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.clearContext();

        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        usuario = User.withUsername("usuario@email.com")
                .password("senha")
                .roles("USER")
                .build();
    }

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void deveSeguirSemAutenticarQuandoNaoHaHeaderAuthorization() throws Exception {

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void deveSeguirSemAutenticarQuandoHeaderNaoForBearer() throws Exception {

        request.addHeader("Authorization", "Basic dXN1YXJpbzpzZW5oYQ==");

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void deveAutenticarQuandoTokenForValido() throws Exception {

        request.addHeader("Authorization", "Bearer token-valido");

        when(jwtService.extrairEmail("token-valido")).thenReturn("usuario@email.com");
        when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(usuario);
        when(jwtService.tokenValido("token-valido", usuario)).thenReturn(true);

        filter.doFilter(request, response, filterChain);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertSame(usuario, auth.getPrincipal());
        assertTrue(auth.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void naoDeveAutenticarQuandoTokenNaoForValidoParaOUsuario() throws Exception {

        request.addHeader("Authorization", "Bearer token-de-outro");

        when(jwtService.extrairEmail("token-de-outro")).thenReturn("usuario@email.com");
        when(userDetailsService.loadUserByUsername("usuario@email.com")).thenReturn(usuario);
        when(jwtService.tokenValido("token-de-outro", usuario)).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void naoDeveCarregarUsuarioQuandoEmailDoTokenForNulo() throws Exception {

        request.addHeader("Authorization", "Bearer token-sem-subject");

        when(jwtService.extrairEmail("token-sem-subject")).thenReturn(null);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void naoDeveRecarregarUsuarioQuandoJaExisteAutenticacaoNoContexto() throws Exception {

        Authentication jaAutenticado =
                new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(jaAutenticado);

        request.addHeader("Authorization", "Bearer token-valido");
        when(jwtService.extrairEmail("token-valido")).thenReturn("usuario@email.com");

        filter.doFilter(request, response, filterChain);

        assertSame(jaAutenticado, SecurityContextHolder.getContext().getAuthentication());
        verify(userDetailsService, never()).loadUserByUsername(anyString());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveLimparContextoESeguirQuandoTokenForInvalido() throws Exception {

        request.addHeader("Authorization", "Bearer token-quebrado");

        when(jwtService.extrairEmail("token-quebrado")).thenThrow(new JwtException("token inválido"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).tokenValido(anyString(), any());
    }

    @Test
    void deveLimparContextoESeguirQuandoTokenForVazio() throws Exception {

        request.addHeader("Authorization", "Bearer ");

        when(jwtService.extrairEmail("")).thenThrow(new IllegalArgumentException("token vazio"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void deveSeguirSemAutenticarQuandoUsuarioDoTokenNaoExiste() throws Exception {

        request.addHeader("Authorization", "Bearer token-valido");

        when(jwtService.extrairEmail("token-valido")).thenReturn("removido@email.com");
        when(userDetailsService.loadUserByUsername("removido@email.com"))
                .thenThrow(new UsernameNotFoundException("Usuário não encontrado"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }
}
