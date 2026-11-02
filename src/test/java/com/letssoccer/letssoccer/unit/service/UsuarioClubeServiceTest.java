package com.letssoccer.letssoccer.unit.service;

import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import com.letssoccer.letssoccer.service.UsuarioClubeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsuarioClubeServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ClubeRepository clubeRepository;

    @InjectMocks
    private UsuarioClubeService service;

    private UsuarioEntity usuario;
    private ClubeEntity clube;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setEmail("usuario@email.com");

        clube = new ClubeEntity();
        clube.setId(10);
        clube.setNome("Cruzeiro");
    }

    @Test
    void deveSelecionarClubeComSucesso() {

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(clubeRepository.findById(10)).thenReturn(Optional.of(clube));

        service.selecionarClube(1L, 10);

        assertSame(clube, usuario.getClube());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioNaoExiste() {

        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.selecionarClube(99L, 10));

        assertEquals("Usuário não encontrado", ex.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoUsuarioJaPossuiClube() {

        usuario.setClube(clube);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.selecionarClube(1L, 20));

        assertEquals("Usuário já possui um clube definido!", ex.getMessage());
        assertSame(clube, usuario.getClube()); // continua com o clube original
        verify(clubeRepository, never()).findById(any());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoClubeNaoExiste() {

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(clubeRepository.findById(99)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.selecionarClube(1L, 99));

        assertEquals("Clube não encontrado", ex.getMessage());
        assertNull(usuario.getClube());
        verify(usuarioRepository, never()).save(any());
    }
}
