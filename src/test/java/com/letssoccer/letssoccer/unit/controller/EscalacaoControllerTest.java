package com.letssoccer.letssoccer.unit.controller;

import com.letssoccer.letssoccer.controller.EscalacaoController;
import com.letssoccer.letssoccer.dto.EscalacaoRequestDto;
import com.letssoccer.letssoccer.dto.EscalacaoResponseDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.service.EscalacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Chama o controller diretamente porque os métodos recebem o usuário logado via
 * {@code @AuthenticationPrincipal}, que não é preenchido em testes com filtros desligados.
 */
class EscalacaoControllerTest {

    private static final String EMAIL = "usuario@email.com";

    @Mock
    private EscalacaoService escalacaoService;

    @InjectMocks
    private EscalacaoController controller;

    private UserDetails usuarioLogado;
    private EscalacaoRequestDto dto;
    private List<JogadorResponseDto> jogadores;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuarioLogado = User.withUsername(EMAIL).password("senha").roles("USER").build();

        dto = new EscalacaoRequestDto();
        dto.setJogadoresIds(IntStream.rangeClosed(1, 11).boxed().collect(Collectors.toList()));

        jogadores = IntStream.rangeClosed(1, 11)
                .mapToObj(i -> new JogadorResponseDto(i, "Jogador " + i, "Meia", "foto-" + i + ".png"))
                .toList();
    }

    @Test
    void deveSalvarEscalacaoERetornarMensagemDeSucesso() {

        when(escalacaoService.salvarEscalacao(1, "4-4-2", dto, EMAIL)).thenReturn(jogadores);

        ResponseEntity<EscalacaoResponseDto> response =
                controller.salvarEscalacao(1, "4-4-2", dto, usuarioLogado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Escalação salva com sucesso!", response.getBody().mensagem());
        assertEquals("4-4-2", response.getBody().esquema());
        assertEquals(11, response.getBody().jogadores().size());
        verify(escalacaoService).salvarEscalacao(1, "4-4-2", dto, EMAIL);
    }

    @Test
    void devePropagarExcecaoQuandoUsuarioJaPossuiEscalacao() {

        when(escalacaoService.salvarEscalacao(1, "4-4-2", dto, EMAIL))
                .thenThrow(new BadRequestException("Você já possui uma escalação salva para o clube"));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> controller.salvarEscalacao(1, "4-4-2", dto, usuarioLogado));

        assertEquals("Você já possui uma escalação salva para o clube", ex.getMessage());
    }

    @Test
    void deveAtualizarEscalacaoERetornarMensagemDeSucesso() {

        when(escalacaoService.atualizarEscalacao(1, "3-5-2", dto, EMAIL)).thenReturn(jogadores);

        ResponseEntity<EscalacaoResponseDto> response =
                controller.atualizarEscalacao(1, "3-5-2", dto, usuarioLogado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Escalação atualizada com sucesso!", response.getBody().mensagem());
        assertEquals("3-5-2", response.getBody().esquema());
        assertEquals(11, response.getBody().jogadores().size());
        verify(escalacaoService).atualizarEscalacao(1, "3-5-2", dto, EMAIL);
    }

    @Test
    void devePropagarExcecaoQuandoNaoHaEscalacaoParaAtualizar() {

        when(escalacaoService.atualizarEscalacao(1, "3-5-2", dto, EMAIL))
                .thenThrow(new NotFoundException("Você ainda não possui escalação para editar"));

        assertThrows(NotFoundException.class,
                () -> controller.atualizarEscalacao(1, "3-5-2", dto, usuarioLogado));
    }

    @Test
    void deveRetornarEscalacaoDoUsuario() {

        EscalacaoResponseDto escalacao = new EscalacaoResponseDto("Escalação encontrada", "4-4-2", jogadores);

        when(escalacaoService.buscarEscalacao(1, EMAIL)).thenReturn(Optional.of(escalacao));

        ResponseEntity<EscalacaoResponseDto> response = controller.buscarEscalacao(1, usuarioLogado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertSame(escalacao, response.getBody());
    }

    @Test
    void deveRetornar204QuandoUsuarioNaoTemEscalacao() {

        when(escalacaoService.buscarEscalacao(1, EMAIL)).thenReturn(Optional.empty());

        ResponseEntity<EscalacaoResponseDto> response = controller.buscarEscalacao(1, usuarioLogado);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }
}
