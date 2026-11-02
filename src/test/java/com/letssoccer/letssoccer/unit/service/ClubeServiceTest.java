package com.letssoccer.letssoccer.unit.service;

import com.letssoccer.letssoccer.dto.ClubeDetalhadoResponseDto;
import com.letssoccer.letssoccer.dto.ClubeRequestDto;
import com.letssoccer.letssoccer.dto.ClubeResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.JogadorEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.JogadorRepository;
import com.letssoccer.letssoccer.service.ClubeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ClubeServiceTest {

    @Mock
    private ClubeRepository clubeRepository;

    @Mock
    private JogadorRepository jogadorRepository;

    @InjectMocks
    private ClubeService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    private ClubeEntity clube(Integer id, String nome, String informacao) {
        ClubeEntity clube = new ClubeEntity();
        clube.setId(id);
        clube.setNome(nome);
        clube.setInformacao(informacao);
        return clube;
    }

    private JogadorEntity jogador(Integer id, String nome, ClubeEntity clube) {
        JogadorEntity jogador = new JogadorEntity();
        jogador.setId(id);
        jogador.setNome(nome);
        jogador.setPosicao("Atacante");
        jogador.setFoto(nome + ".png");
        jogador.setClube(clube);
        return jogador;
    }

    // ===== criarClube =====

    @Test
    void deveCriarClubeComSucesso() {

        ClubeRequestDto dto = new ClubeRequestDto("Cruzeiro", "Fundado em 1921");

        when(clubeRepository.existsByNome("Cruzeiro")).thenReturn(false);
        when(clubeRepository.save(any(ClubeEntity.class))).thenAnswer(inv -> {
            ClubeEntity entity = inv.getArgument(0);
            entity.setId(1);
            return entity;
        });

        ClubeResponseDto response = service.criarClube(dto);

        ArgumentCaptor<ClubeEntity> captor = ArgumentCaptor.forClass(ClubeEntity.class);
        verify(clubeRepository).save(captor.capture());
        assertEquals("Cruzeiro", captor.getValue().getNome());
        assertEquals("Fundado em 1921", captor.getValue().getInformacao());

        assertEquals(1, response.id());
        assertEquals("Cruzeiro", response.nome());
        assertEquals("Fundado em 1921", response.informacao());
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deveLancarExcecaoQuandoNomeDoClubeInvalido(String nomeInvalido) {

        ClubeRequestDto dto = new ClubeRequestDto(nomeInvalido, "Informação");

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.criarClube(dto));

        assertEquals(KeyMessages.NOME_CLUBE_OBRIGATORIO, ex.getMessage());
        verify(clubeRepository, never()).save(any());
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deveLancarExcecaoQuandoInformacaoDoClubeInvalida(String informacaoInvalida) {

        ClubeRequestDto dto = new ClubeRequestDto("Cruzeiro", informacaoInvalida);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.criarClube(dto));

        assertEquals(KeyMessages.INFORMACAO_CLUBE_OBRIGATORIO, ex.getMessage());
        verify(clubeRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoNomeDoClubeJaCadastrado() {

        ClubeRequestDto dto = new ClubeRequestDto("Cruzeiro", "Informação");

        when(clubeRepository.existsByNome("Cruzeiro")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.criarClube(dto));

        assertEquals(KeyMessages.NOME_CLUBE_CADASTRADO, ex.getMessage());
        verify(clubeRepository, never()).save(any());
    }

    // ===== listarClubes =====

    @Test
    void deveListarClubes() {

        when(clubeRepository.findAll()).thenReturn(List.of(
                clube(1, "Cruzeiro", "Info 1"),
                clube(2, "Atlético", "Info 2")
        ));

        List<ClubeResponseDto> clubes = service.listarClubes();

        assertEquals(2, clubes.size());
        assertEquals("Cruzeiro", clubes.get(0).nome());
        assertEquals("Atlético", clubes.get(1).nome());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHaClubes() {

        when(clubeRepository.findAll()).thenReturn(List.of());

        assertTrue(service.listarClubes().isEmpty());
    }

    // ===== buscarClubePorId =====

    @Test
    void deveBuscarClubePorId() {

        when(clubeRepository.findById(1)).thenReturn(Optional.of(clube(1, "Cruzeiro", "Info")));

        ClubeResponseDto response = service.buscarClubePorId(1);

        assertEquals(1, response.id());
        assertEquals("Cruzeiro", response.nome());
        assertEquals("Info", response.informacao());
    }

    @Test
    void deveLancarNotFoundQuandoClubeNaoExisteAoBuscarPorId() {

        when(clubeRepository.findById(99)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.buscarClubePorId(99));

        assertEquals(KeyMessages.CLUBE_NAO_ENCONTRADO, ex.getMessage());
    }

    // ===== atualizarClube =====

    @Test
    void deveAtualizarClubeComSucesso() {

        ClubeEntity existente = clube(1, "Cruzeiro", "Info antiga");
        ClubeRequestDto dto = new ClubeRequestDto("Cruzeiro EC", "Info nova");

        when(clubeRepository.findById(1)).thenReturn(Optional.of(existente));
        when(clubeRepository.save(any(ClubeEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ClubeResponseDto response = service.atualizarClube(1, dto);

        assertEquals(1, response.id());
        assertEquals("Cruzeiro EC", response.nome());
        assertEquals("Info nova", response.informacao());
        verify(clubeRepository).save(existente);
    }

    @Test
    void devePermitirAtualizarQuandoNomeContinuaOMesmoDoProprioClube() {

        ClubeEntity existente = clube(1, "Cruzeiro", "Info antiga");
        ClubeRequestDto dto = new ClubeRequestDto("Cruzeiro", "Info nova");

        when(clubeRepository.findById(1)).thenReturn(Optional.of(existente));
        when(clubeRepository.existsByNome("Cruzeiro")).thenReturn(true);
        when(clubeRepository.save(any(ClubeEntity.class))).thenAnswer(inv -> inv.getArgument(0));

        ClubeResponseDto response = service.atualizarClube(1, dto);

        assertEquals("Cruzeiro", response.nome());
        assertEquals("Info nova", response.informacao());
    }

    @Test
    void deveLancarExcecaoQuandoNovoNomeJaPertenceAOutroClube() {

        ClubeEntity existente = clube(1, "Cruzeiro", "Info");
        ClubeRequestDto dto = new ClubeRequestDto("Atlético", "Info");

        when(clubeRepository.findById(1)).thenReturn(Optional.of(existente));
        when(clubeRepository.existsByNome("Atlético")).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.atualizarClube(1, dto));

        assertEquals(KeyMessages.NOME_CLUBE_CADASTRADO, ex.getMessage());
        verify(clubeRepository, never()).save(any());
    }

    @Test
    void deveLancarNotFoundQuandoClubeNaoExisteAoAtualizar() {

        when(clubeRepository.findById(99)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.atualizarClube(99, new ClubeRequestDto("Nome", "Info")));

        assertEquals(KeyMessages.CLUBE_NAO_ENCONTRADO, ex.getMessage());
        verify(clubeRepository, never()).save(any());
    }

    // ===== deletarClube =====

    @Test
    void deveDeletarClubeSemJogadores() {

        when(clubeRepository.findByIdWithJogadores(1))
                .thenReturn(Optional.of(clube(1, "Cruzeiro", "Info")));

        service.deletarClube(1);

        verify(clubeRepository).deleteById(1);
    }

    @Test
    void deveLancarNotFoundQuandoClubeNaoExisteAoDeletar() {

        when(clubeRepository.findByIdWithJogadores(99)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.deletarClube(99));

        assertEquals("Clube com ID 99 não encontrado.", ex.getMessage());
        verify(clubeRepository, never()).deleteById(any());
    }

    @Test
    void deveLancarExcecaoAoDeletarClubeComJogadores() {

        ClubeEntity comJogadores = clube(1, "Cruzeiro", "Info");
        comJogadores.setJogadores(List.of(jogador(10, "Gabigol", comJogadores)));

        when(clubeRepository.findByIdWithJogadores(1)).thenReturn(Optional.of(comJogadores));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.deletarClube(1));

        assertEquals(KeyMessages.NAO_PERMITE_EXCLUSAO_CLUBE_COM_JOGADOR, ex.getMessage());
        verify(clubeRepository, never()).deleteById(any());
    }

    @Test
    void deveDeletarClubeQuandoListaDeJogadoresForNula() {

        ClubeEntity semLista = clube(1, "Cruzeiro", "Info");
        semLista.setJogadores(null);

        when(clubeRepository.findByIdWithJogadores(1)).thenReturn(Optional.of(semLista));

        service.deletarClube(1);

        verify(clubeRepository).deleteById(1);
    }

    // ===== deletarClubes =====

    @Test
    void deveDeletarTodosOsClubesQuandoNaoHaJogadores() {

        when(jogadorRepository.count()).thenReturn(0L);

        service.deletarClubes();

        verify(clubeRepository).deleteAll();
    }

    @Test
    void deveLancarExcecaoAoDeletarTodosOsClubesQuandoHaJogadores() {

        when(jogadorRepository.count()).thenReturn(3L);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.deletarClubes());

        assertEquals(KeyMessages.NAO_PERMITE_EXCLUSAO_CLUBE_COM_JOGADOR, ex.getMessage());
        verify(clubeRepository, never()).deleteAll();
    }

    // ===== buscarClubeDetalhado =====

    @Test
    void deveBuscarClubeDetalhadoComJogadores() {

        ClubeEntity cruzeiro = clube(1, "Cruzeiro", "Info");
        cruzeiro.setJogadores(List.of(
                jogador(10, "Cássio", cruzeiro),
                jogador(11, "Kaio Jorge", cruzeiro)
        ));

        when(clubeRepository.findByIdWithJogadores(1)).thenReturn(Optional.of(cruzeiro));

        ClubeDetalhadoResponseDto response = service.buscarClubeDetalhado(1);

        assertEquals(1, response.id());
        assertEquals("Cruzeiro", response.nome());
        assertEquals(2, response.jogadores().size());
        assertEquals("Cássio", response.jogadores().get(0).nome());
        assertEquals("Kaio Jorge", response.jogadores().get(1).nome());
    }

    @Test
    void deveLancarNotFoundQuandoClubeNaoExisteAoBuscarDetalhado() {

        when(clubeRepository.findByIdWithJogadores(99)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.buscarClubeDetalhado(99));

        assertEquals(KeyMessages.CLUBE_NAO_ENCONTRADO, ex.getMessage());
    }
}
