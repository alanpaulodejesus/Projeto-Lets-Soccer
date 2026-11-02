package com.letssoccer.letssoccer.unit.service;

import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import com.letssoccer.letssoccer.service.Perfil;
import com.letssoccer.letssoccer.service.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    @Mock
    private UsuarioRepository repository;

    @InjectMocks
    private UserDetailsServiceImpl service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void deveCarregarUsuarioPorEmail() {

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setEmail("usuario@email.com");
        usuario.setSenha("senhaCriptografada");
        usuario.setPerfil(Perfil.USER);

        when(repository.findByEmail("usuario@email.com")).thenReturn(Optional.of(usuario));

        UserDetails userDetails = service.loadUserByUsername("usuario@email.com");

        assertEquals("usuario@email.com", userDetails.getUsername());
        assertEquals("senhaCriptografada", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {

        when(repository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        UsernameNotFoundException ex = assertThrows(UsernameNotFoundException.class,
                () -> service.loadUserByUsername("inexistente@email.com"));

        assertEquals("Usuário não encontrado", ex.getMessage());
    }
}
