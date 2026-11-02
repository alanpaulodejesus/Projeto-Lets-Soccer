package com.letssoccer.letssoccer.unit.controller;

import com.letssoccer.letssoccer.controller.UsuarioClubeController;
import com.letssoccer.letssoccer.dto.UsuarioClubeRequestDto;
import com.letssoccer.letssoccer.dto.UsuarioClubeResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.sucess.MessageResponseDto;
import com.letssoccer.letssoccer.service.UsuarioClubeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Chama o controller diretamente porque o usuário logado chega via {@code @AuthenticationPrincipal}.
 */
class UsuarioClubeControllerTest {

    @Mock
    private UsuarioClubeService service;

    @InjectMocks
    private UsuarioClubeController controller;

    private UsuarioEntity usuario;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setEmail("usuario@email.com");
    }

    @Test
    void deveSelecionarClubeERetornarMensagemDeSucesso() {

        ResponseEntity<MessageResponseDto> response =
                controller.selecionarClube(usuario, new UsuarioClubeRequestDto(10));

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Clube definido com sucesso!", response.getBody().mensagem());
        verify(service).selecionarClube(1L, 10);
    }

    @Test
    void devePropagarExcecaoQuandoUsuarioJaPossuiClube() {

        doThrow(new BadRequestException("Usuário já possui um clube definido!"))
                .when(service).selecionarClube(1L, 10);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> controller.selecionarClube(usuario, new UsuarioClubeRequestDto(10)));

        assertEquals("Usuário já possui um clube definido!", ex.getMessage());
    }

    @Test
    void deveRetornarClubeDoUsuario() {

        ClubeEntity clube = new ClubeEntity();
        clube.setId(10);
        clube.setNome("Cruzeiro");
        usuario.setClube(clube);

        ResponseEntity<UsuarioClubeResponseDto> response = controller.buscarClube(usuario);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(10, response.getBody().id());
        assertEquals("Cruzeiro", response.getBody().nome());
    }

    @Test
    void deveRetornar204QuandoUsuarioNaoTemClube() {

        ResponseEntity<UsuarioClubeResponseDto> response = controller.buscarClube(usuario);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
