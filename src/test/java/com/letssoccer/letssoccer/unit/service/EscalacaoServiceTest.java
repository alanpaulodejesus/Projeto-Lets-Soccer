package com.letssoccer.letssoccer.unit.service;

import com.letssoccer.letssoccer.dto.EscalacaoRequestDto;
import com.letssoccer.letssoccer.dto.EscalacaoResponseDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.EscalacaoEntity;
import com.letssoccer.letssoccer.entities.JogadorEntity;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.EscalacaoRepository;
import com.letssoccer.letssoccer.repositories.JogadorRepository;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import com.letssoccer.letssoccer.service.EscalacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EscalacaoServiceTest {

    private static final String EMAIL = "usuario@email.com";
    private static final Integer CLUBE_ID = 1;
    private static final String ESQUEMA = "4-4-2";

    @Mock
    private JogadorRepository jogadorRepository;

    @Mock
    private ClubeRepository clubeRepository;

    @Mock
    private EscalacaoRepository escalacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EscalacaoService service;

    private ClubeEntity clube;
    private UsuarioEntity usuario;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        clube = new ClubeEntity();
        clube.setId(CLUBE_ID);
        clube.setNome("Cruzeiro");
        clube.setInformacao("Info");

        usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setEmail(EMAIL);
    }

    // ===== helpers =====

    private JogadorEntity jogador(Integer id, ClubeEntity doClube) {
        JogadorEntity jogador = new JogadorEntity();
        jogador.setId(id);
        jogador.setNome("Jogador " + id);
        jogador.setPosicao("Meia");
        jogador.setFoto("jogador-" + id + ".png");
        jogador.setClube(doClube);
        return jogador;
    }

    private List<Integer> idsDe1a11() {
        return IntStream.rangeClosed(1, 11).boxed().collect(Collectors.toList());
    }

    private List<JogadorEntity> jogadoresDoClube(List<Integer> ids) {
        return ids.stream().map(id -> jogador(id, clube)).collect(Collectors.toList());
    }

    private EscalacaoRequestDto request(List<Integer> ids) {
        EscalacaoRequestDto dto = new EscalacaoRequestDto();
        dto.setJogadoresIds(ids);
        return dto;
    }

    private void usuarioEClubeExistem() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(clubeRepository.findById(CLUBE_ID)).thenReturn(Optional.of(clube));
        when(escalacaoRepository.existsByClubeIdAndUsuarioEmail(CLUBE_ID, EMAIL)).thenReturn(false);
    }

    private EscalacaoEntity escalacaoExistente(List<Integer> ids) {
        EscalacaoEntity escalacao = new EscalacaoEntity();
        escalacao.setUsuario(usuario);
        escalacao.setClube(clube);
        escalacao.setEsquema("3-5-2");
        escalacao.setJogadores(jogadoresDoClube(ids));
        return escalacao;
    }

    // ===== salvarEscalacao =====

    @Test
    void deveSalvarEscalacaoComSucesso() {

        List<Integer> ids = idsDe1a11();

        usuarioEClubeExistem();
        when(jogadorRepository.findAllById(any())).thenReturn(jogadoresDoClube(ids));
        when(escalacaoRepository.save(any(EscalacaoEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<JogadorResponseDto> resposta =
                service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(ids), EMAIL);

        ArgumentCaptor<EscalacaoEntity> captor = ArgumentCaptor.forClass(EscalacaoEntity.class);
        verify(escalacaoRepository).save(captor.capture());
        EscalacaoEntity salva = captor.getValue();

        assertSame(usuario, salva.getUsuario());
        assertSame(clube, salva.getClube());
        assertEquals(ESQUEMA, salva.getEsquema());
        assertEquals(11, salva.getJogadores().size());

        assertEquals(11, resposta.size());
        assertEquals("Jogador 1", resposta.get(0).nome());
        assertEquals("Meia", resposta.get(0).posicao());
        assertEquals("jogador-1.png", resposta.get(0).foto());
    }

    @Test
    void deveManterAOrdemDosJogadoresEnviadaNaRequisicao() {

        List<Integer> idsNaOrdemEscolhida = idsDe1a11();
        Collections.reverse(idsNaOrdemEscolhida); // 11, 10, ..., 1

        // o repositório devolve em outra ordem (crescente)
        List<JogadorEntity> retornoDoBanco = jogadoresDoClube(idsDe1a11());

        usuarioEClubeExistem();
        when(jogadorRepository.findAllById(any())).thenReturn(retornoDoBanco);
        when(escalacaoRepository.save(any(EscalacaoEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<JogadorResponseDto> resposta =
                service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(idsNaOrdemEscolhida), EMAIL);

        List<Integer> idsRetornados = resposta.stream().map(JogadorResponseDto::id).toList();
        assertEquals(idsNaOrdemEscolhida, idsRetornados);
    }

    @Test
    void deveLancarNotFoundQuandoUsuarioNaoExisteAoSalvarEscalacao() {

        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(idsDe1a11()), EMAIL));

        assertEquals("Usuário não encontrado", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarNotFoundQuandoClubeNaoExisteAoSalvarEscalacao() {

        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(clubeRepository.findById(CLUBE_ID)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(idsDe1a11()), EMAIL));

        assertEquals(KeyMessages.CLUBE_NAO_ENCONTRADO, ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    @Test
    void deveBarrarNovaEscalacaoQuandoUsuarioJaPossuiUmaParaOClube() {

        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(clubeRepository.findById(CLUBE_ID)).thenReturn(Optional.of(clube));
        when(escalacaoRepository.existsByClubeIdAndUsuarioEmail(CLUBE_ID, EMAIL)).thenReturn(true);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(idsDe1a11()), EMAIL));

        assertEquals("Você já possui uma escalação salva para o clube", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoListaDeJogadoresForNulaAoSalvar() {

        usuarioEClubeExistem();

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(null), EMAIL));

        assertEquals("A escalação deve conter exatamente 11 jogadores", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoHouverMenosDe11Jogadores() {

        usuarioEClubeExistem();
        List<Integer> dezIds = idsDe1a11().subList(0, 10);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(dezIds), EMAIL));

        assertEquals("A escalação deve conter exatamente 11 jogadores", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoHouverMaisDe11Jogadores() {

        usuarioEClubeExistem();
        List<Integer> dozeIds = IntStream.rangeClosed(1, 12).boxed().collect(Collectors.toList());

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(dozeIds), EMAIL));

        assertEquals("A escalação deve conter exatamente 11 jogadores", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoHouverJogadoresDuplicados() {

        usuarioEClubeExistem();
        List<Integer> comDuplicado = idsDe1a11();
        comDuplicado.set(10, 1); // 11 posições, mas só 10 ids únicos

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(comDuplicado), EMAIL));

        assertEquals("Não é permitido jogadores duplicados", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoAlgumJogadorNaoForEncontrado() {

        List<Integer> ids = idsDe1a11();

        usuarioEClubeExistem();
        // o banco só conhece 10 dos 11 ids enviados
        when(jogadorRepository.findAllById(any()))
                .thenReturn(jogadoresDoClube(ids.subList(0, 10)));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(ids), EMAIL));

        assertEquals("Um ou mais jogadores não foram encontrados", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoQuandoAlgumJogadorForDeOutroClube() {

        List<Integer> ids = idsDe1a11();

        ClubeEntity outroClube = new ClubeEntity();
        outroClube.setId(2);

        List<JogadorEntity> jogadores = jogadoresDoClube(ids);
        jogadores.set(5, jogador(6, outroClube));

        usuarioEClubeExistem();
        when(jogadorRepository.findAllById(any())).thenReturn(jogadores);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.salvarEscalacao(CLUBE_ID, ESQUEMA, request(ids), EMAIL));

        assertEquals("Todos os jogadores devem pertencer ao clube informado", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    // ===== atualizarEscalacao =====

    @Test
    void deveAtualizarEscalacaoComSucesso() {

        EscalacaoEntity existente = escalacaoExistente(idsDe1a11());

        List<Integer> novosIds = IntStream.rangeClosed(12, 22).boxed().collect(Collectors.toList());

        when(escalacaoRepository.findTopByClubeIdAndUsuarioEmailOrderByDataCriacaoDesc(CLUBE_ID, EMAIL))
                .thenReturn(Optional.of(existente));
        when(jogadorRepository.findAllById(any())).thenReturn(jogadoresDoClube(novosIds));
        when(escalacaoRepository.save(any(EscalacaoEntity.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        List<JogadorResponseDto> resposta =
                service.atualizarEscalacao(CLUBE_ID, ESQUEMA, request(novosIds), EMAIL);

        // atualiza a escalação existente em vez de criar outra
        verify(escalacaoRepository).save(existente);
        assertEquals(ESQUEMA, existente.getEsquema());
        assertEquals(11, existente.getJogadores().size());
        assertEquals(12, existente.getJogadores().get(0).getId());

        assertEquals(11, resposta.size());
        assertEquals(12, resposta.get(0).id());
        assertEquals(22, resposta.get(10).id());
    }

    @Test
    void deveLancarNotFoundQuandoNaoHaEscalacaoParaEditar() {

        when(escalacaoRepository.findTopByClubeIdAndUsuarioEmailOrderByDataCriacaoDesc(CLUBE_ID, EMAIL))
                .thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.atualizarEscalacao(CLUBE_ID, ESQUEMA, request(idsDe1a11()), EMAIL));

        assertEquals("Você ainda não possui escalação para editar", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    @Test
    void deveLancarExcecaoAoAtualizarComQuantidadeDeJogadoresInvalida() {

        EscalacaoEntity existente = escalacaoExistente(idsDe1a11());

        when(escalacaoRepository.findTopByClubeIdAndUsuarioEmailOrderByDataCriacaoDesc(CLUBE_ID, EMAIL))
                .thenReturn(Optional.of(existente));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.atualizarEscalacao(CLUBE_ID, ESQUEMA,
                        request(new ArrayList<>(idsDe1a11().subList(0, 5))), EMAIL));

        assertEquals("A escalação deve conter exatamente 11 jogadores", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
        assertEquals("3-5-2", existente.getEsquema()); // não alterou nada
    }

    @Test
    void deveLancarExcecaoAoAtualizarComJogadorDeOutroClube() {

        List<Integer> ids = idsDe1a11();
        EscalacaoEntity existente = escalacaoExistente(ids);

        ClubeEntity outroClube = new ClubeEntity();
        outroClube.setId(2);
        List<JogadorEntity> jogadores = jogadoresDoClube(ids);
        jogadores.set(0, jogador(1, outroClube));

        when(escalacaoRepository.findTopByClubeIdAndUsuarioEmailOrderByDataCriacaoDesc(CLUBE_ID, EMAIL))
                .thenReturn(Optional.of(existente));
        when(jogadorRepository.findAllById(any())).thenReturn(jogadores);

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.atualizarEscalacao(CLUBE_ID, ESQUEMA, request(ids), EMAIL));

        assertEquals("Todos os jogadores devem pertencer ao clube informado", ex.getMessage());
        verify(escalacaoRepository, never()).save(any());
    }

    // ===== buscarEscalacao =====

    @Test
    void deveBuscarEscalacaoDoUsuario() {

        EscalacaoEntity existente = escalacaoExistente(idsDe1a11());

        when(escalacaoRepository.findTopByClubeIdAndUsuarioEmailOrderByDataCriacaoDesc(CLUBE_ID, EMAIL))
                .thenReturn(Optional.of(existente));

        Optional<EscalacaoResponseDto> resultado = service.buscarEscalacao(CLUBE_ID, EMAIL);

        assertTrue(resultado.isPresent());
        assertEquals("Escalação encontrada", resultado.get().mensagem());
        assertEquals("3-5-2", resultado.get().esquema());
        assertEquals(11, resultado.get().jogadores().size());
        assertEquals("Jogador 1", resultado.get().jogadores().get(0).nome());
    }

    @Test
    void deveRetornarVazioQuandoUsuarioNaoTemEscalacao() {

        when(escalacaoRepository.findTopByClubeIdAndUsuarioEmailOrderByDataCriacaoDesc(CLUBE_ID, EMAIL))
                .thenReturn(Optional.empty());

        assertTrue(service.buscarEscalacao(CLUBE_ID, EMAIL).isEmpty());
    }
}
