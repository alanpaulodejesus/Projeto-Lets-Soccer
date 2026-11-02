package com.letssoccer.letssoccer.unit.service;

import com.letssoccer.letssoccer.dto.JogadorRequestDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.JogadorEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.JogadorRepository;
import com.letssoccer.letssoccer.service.JogadorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JogadorServiceTest {

    @Mock
    private JogadorRepository jogadorRepository;

    @Mock
    private ClubeRepository clubeRepository;

    @InjectMocks
    private JogadorService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private ClubeEntity clube(Integer id) {
        ClubeEntity clube = new ClubeEntity();
        clube.setId(id);
        clube.setNome("Cruzeiro");
        clube.setInformacao("Info");
        return clube;
    }

    private JogadorEntity jogador(Integer id, String nome, ClubeEntity clube) {
        JogadorEntity jogador = new JogadorEntity();
        jogador.setId(id);
        jogador.setNome(nome);
        jogador.setPosicao("Goleiro");
        jogador.setFoto(nome + ".png");
        jogador.setClube(clube);
        return jogador;
    }

    // ===== adicionarJogador =====

    @Test
    void deveAdicionarJogadorComSucesso() {

        ClubeEntity clube = clube(1);
        JogadorRequestDto dto = new JogadorRequestDto("Cássio", "Goleiro");

        when(clubeRepository.findById(1)).thenReturn(Optional.of(clube));
        when(jogadorRepository.existsByNomeIgnoreCaseAndClubeId("Cássio", 1)).thenReturn(false);
        when(jogadorRepository.save(any(JogadorEntity.class))).thenAnswer(inv -> {
            JogadorEntity entity = inv.getArgument(0);
            entity.setId(10);
            return entity;
        });

        JogadorResponseDto response = service.adicionarJogador(1, dto);

        ArgumentCaptor<JogadorEntity> captor = ArgumentCaptor.forClass(JogadorEntity.class);
        verify(jogadorRepository).save(captor.capture());
        assertEquals("Cássio", captor.getValue().getNome());
        assertEquals("Goleiro", captor.getValue().getPosicao());
        assertSame(clube, captor.getValue().getClube());

        assertEquals(10, response.id());
        assertEquals("Cássio", response.nome());
        assertEquals("Goleiro", response.posicao());
    }

    @Test
    void deveLancarNotFoundQuandoClubeNaoExisteAoAdicionarJogador() {

        when(clubeRepository.findById(99)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.adicionarJogador(99, new JogadorRequestDto("Cássio", "Goleiro")));

        assertEquals(KeyMessages.CLUBE_NAO_ENCONTRADO, ex.getMessage());
        verify(jogadorRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoJogadorJaExisteNoClube() {

        when(clubeRepository.findById(1)).thenReturn(Optional.of(clube(1)));
        when(jogadorRepository.existsByNomeIgnoreCaseAndClubeId("Cássio", 1)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.adicionarJogador(1, new JogadorRequestDto("Cássio", "Goleiro")));

        assertEquals(KeyMessages.DUPLICIDADE_NOME_JOGADOR, ex.getMessage());
        verify(jogadorRepository, never()).save(any());
    }

    // ===== listarJogadoresDoClube =====

    @Test
    void deveListarJogadoresDoClube() {

        ClubeEntity clube = clube(1);

        when(clubeRepository.existsById(1)).thenReturn(true);
        when(jogadorRepository.findByClubeId(1)).thenReturn(List.of(
                jogador(10, "Cássio", clube),
                jogador(11, "Fagner", clube)
        ));

        List<JogadorResponseDto> jogadores = service.listarJogadoresDoClube(1);

        assertEquals(2, jogadores.size());
        assertEquals("Cássio", jogadores.get(0).nome());
        assertEquals("Fagner", jogadores.get(1).nome());
        assertEquals("Fagner.png", jogadores.get(1).foto());
    }

    @Test
    void deveRetornarListaVaziaQuandoClubeNaoTemJogadores() {

        when(clubeRepository.existsById(1)).thenReturn(true);
        when(jogadorRepository.findByClubeId(1)).thenReturn(List.of());

        assertTrue(service.listarJogadoresDoClube(1).isEmpty());
    }

    @Test
    void deveLancarNotFoundQuandoClubeNaoExisteAoListarJogadores() {

        when(clubeRepository.existsById(99)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> service.listarJogadoresDoClube(99));

        verify(jogadorRepository, never()).findByClubeId(any());
    }

    // ===== deletarJogador =====

    @Test
    void deveDeletarJogador() {

        when(jogadorRepository.existsById(10)).thenReturn(true);

        service.deletarJogador(10);

        verify(jogadorRepository).deleteById(10);
    }

    @Test
    void deveLancarNotFoundQuandoJogadorNaoExisteAoDeletar() {

        when(jogadorRepository.existsById(99)).thenReturn(false);

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.deletarJogador(99));

        assertEquals("Jogador com ID 99 não encontrado.", ex.getMessage());
        verify(jogadorRepository, never()).deleteById(any());
    }
}
