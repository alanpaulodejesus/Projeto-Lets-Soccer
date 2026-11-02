package com.letssoccer.letssoccer.unit.service;

import com.letssoccer.letssoccer.service.JwtService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTest {

    // mesma chave usada pelo JwtService; necessária para montar tokens expirados no teste
    private static final String SECRET_KEY =
            "letsSoccer_Secret_Key_Para_Desenvolvimento_123456";

    private JwtService service;
    private UserDetails usuario;

    @BeforeEach
    void setup() {
        service = new JwtService();
        usuario = User.withUsername("usuario@email.com")
                .password("senha")
                .roles("USER")
                .build();
    }

    private String tokenExpirado(String email) {
        long umaHoraAtras = System.currentTimeMillis() - 60 * 60 * 1000;
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date(umaHoraAtras - 1000))
                .setExpiration(new Date(umaHoraAtras))
                .signWith(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()), SignatureAlgorithm.HS256)
                .compact();
    }

    @Test
    void deveGerarTokenNaoVazioComTresPartes() {

        String token = service.gerarToken(usuario);

        assertNotNull(token);
        assertEquals(3, token.split("\\.").length);
    }

    @Test
    void deveExtrairOEmailDoToken() {

        String token = service.gerarToken(usuario);

        assertEquals("usuario@email.com", service.extrairEmail(token));
    }

    @Test
    void deveValidarTokenDoProprioUsuario() {

        String token = service.gerarToken(usuario);

        assertTrue(service.tokenValido(token, usuario));
    }

    @Test
    void deveInvalidarTokenDeOutroUsuario() {

        String token = service.gerarToken(usuario);
        UserDetails outro = User.withUsername("outro@email.com")
                .password("senha")
                .roles("USER")
                .build();

        assertFalse(service.tokenValido(token, outro));
    }

    @Test
    void deveLancarExcecaoAoExtrairEmailDeTokenExpirado() {

        String expirado = tokenExpirado("usuario@email.com");

        assertThrows(ExpiredJwtException.class, () -> service.extrairEmail(expirado));
    }

    @Test
    void deveLancarExcecaoAoValidarTokenExpirado() {

        // o parse do jjwt já rejeita tokens expirados; o JwtAuthenticationFilter captura JwtException
        String expirado = tokenExpirado("usuario@email.com");

        assertThrows(JwtException.class, () -> service.tokenValido(expirado, usuario));
    }

    @Test
    void deveLancarExcecaoQuandoTokenForAssinadoComOutraChave() {

        String tokenOutraChave = Jwts.builder()
                .setSubject("usuario@email.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(Keys.secretKeyFor(SignatureAlgorithm.HS256), SignatureAlgorithm.HS256)
                .compact();

        assertThrows(JwtException.class, () -> service.extrairEmail(tokenOutraChave));
    }

    @Test
    void deveLancarExcecaoQuandoTokenForMalformado() {

        assertThrows(JwtException.class, () -> service.extrairEmail("isso-nao-e-um-jwt"));
    }
}
