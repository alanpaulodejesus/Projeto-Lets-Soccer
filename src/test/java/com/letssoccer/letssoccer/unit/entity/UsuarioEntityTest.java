package com.letssoccer.letssoccer.unit.entity;

import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.service.Perfil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioEntityTest {

    private UsuarioEntity usuario;

    @BeforeEach
    void setup() {
        usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setNome("Alan");
        usuario.setEmail("alan@email.com");
        usuario.setSenha("senhaCriptografada");
        usuario.setPerfil(Perfil.USER);
    }

    @Test
    void deveExporOsDadosPeloGettersESetters() {

        ClubeEntity clube = new ClubeEntity();
        clube.setId(10);
        usuario.setClube(clube);

        assertEquals(1L, usuario.getId());
        assertEquals("Alan", usuario.getNome());
        assertEquals("alan@email.com", usuario.getEmail());
        assertEquals("senhaCriptografada", usuario.getSenha());
        assertEquals(Perfil.USER, usuario.getPerfil());
        assertSame(clube, usuario.getClube());
    }

    @Test
    void deveUsarEmailComoUsernameESenhaComoPassword() {

        assertEquals("alan@email.com", usuario.getUsername());
        assertEquals("senhaCriptografada", usuario.getPassword());
    }

    @Test
    void deveRetornarRoleUserParaPerfilUser() {

        List<String> roles = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertEquals(List.of("ROLE_USER"), roles);
    }

    @Test
    void deveRetornarRoleAdminParaPerfilAdmin() {

        usuario.setPerfil(Perfil.ADMIN);

        List<String> roles = usuario.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        assertEquals(List.of("ROLE_ADMIN"), roles);
    }

    @Test
    void deveNascerAtivoENaoTerClube() {

        assertTrue(usuario.isAtivo());
        assertTrue(usuario.isEnabled());
        assertNull(usuario.getClube());
    }

    @Test
    void deveFicarDesabilitadoQuandoInativo() {

        usuario.setAtivo(false);

        assertFalse(usuario.isAtivo());
        assertFalse(usuario.isEnabled());
    }

    @Test
    void contaNaoDeveEstarExpiradaNemBloqueadaNemComCredenciaisExpiradas() {

        assertTrue(usuario.isAccountNonExpired());
        assertTrue(usuario.isAccountNonLocked());
        assertTrue(usuario.isCredentialsNonExpired());
    }
}
