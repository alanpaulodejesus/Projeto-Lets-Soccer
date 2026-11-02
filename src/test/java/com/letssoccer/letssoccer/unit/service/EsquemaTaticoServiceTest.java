package com.letssoccer.letssoccer.unit.service;

import com.letssoccer.letssoccer.dto.EsquemaTaticoRequestDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.EsquemaTaticoEntity;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.EsquemaTaticoRepository;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import com.letssoccer.letssoccer.service.EsquemaTaticoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EsquemaTaticoServiceTest {

    private static final String EMAIL = "usuario@email.com";
    private static final Integer CLUBE_ID = 1;

    @Mock
    private ClubeRepository clubeRepository;

    @Mock
    private EsquemaTaticoRepository esquemaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private EsquemaTaticoService service;

    private UsuarioEntity usuario;
    private ClubeEntity clube;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        usuario = new UsuarioEntity();
        usuario.setId(1L);
        usuario.setEmail(EMAIL);

        clube = new ClubeEntity();
        clube.setId(CLUBE_ID);
        clube.setNome("Cruzeiro");
    }

    private void usuarioEClubeExistem() {
        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(clubeRepository.findById(CLUBE_ID)).thenReturn(Optional.of(clube));
    }

    @ParameterizedTest
    @CsvSource({
            "true,  false, false, false, 4-4-2",
            "false, true,  false, false, 3-5-2",
            "false, false, true,  false, 5-4-1",
            "false, false, false, true,  2-4-4"
    })
    void deveDefinirEsquemaTaticoComSucesso(boolean e442, boolean e352, boolean e541,
                                            boolean e244, String esperado) {

        usuarioEClubeExistem();
        when(esquemaRepository.findByClubeIdAndUsuarioEmail(CLUBE_ID, EMAIL))
                .thenReturn(Optional.empty());

        String esquema = service.definirEsquema(CLUBE_ID, EMAIL,
                new EsquemaTaticoRequestDto(e442, e352, e541, e244));

        assertEquals(esperado, esquema);

        ArgumentCaptor<EsquemaTaticoEntity> captor = ArgumentCaptor.forClass(EsquemaTaticoEntity.class);
        verify(esquemaRepository).save(captor.capture());
        EsquemaTaticoEntity salvo = captor.getValue();

        assertSame(usuario, salvo.getUsuario());
        assertSame(clube, salvo.getClube());
        assertEquals(e442, salvo.isEsquema442());
        assertEquals(e352, salvo.isEsquema352());
        assertEquals(e541, salvo.isEsquema541());
        assertEquals(e244, salvo.isEsquema244());
    }

    @Test
    void deveAtualizarEsquemaExistenteEmVezDeCriarOutro() {

        EsquemaTaticoEntity existente = new EsquemaTaticoEntity();
        existente.setEsquema442(true);

        usuarioEClubeExistem();
        when(esquemaRepository.findByClubeIdAndUsuarioEmail(CLUBE_ID, EMAIL))
                .thenReturn(Optional.of(existente));

        String esquema = service.definirEsquema(CLUBE_ID, EMAIL,
                new EsquemaTaticoRequestDto(false, true, false, false));

        assertEquals("3-5-2", esquema);

        // reaproveita a mesma entidade e troca o esquema marcado
        verify(esquemaRepository).save(existente);
        assertFalse(existente.isEsquema442());
        assertTrue(existente.isEsquema352());
        assertSame(usuario, existente.getUsuario());
        assertSame(clube, existente.getClube());
    }

    @Test
    void deveLancarExcecaoQuandoNenhumEsquemaForSelecionado() {

        usuarioEClubeExistem();

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.definirEsquema(CLUBE_ID, EMAIL,
                        new EsquemaTaticoRequestDto(false, false, false, false)));

        assertEquals(KeyMessages.OBRIGATORIEDADE_ESQUEMA, ex.getMessage());
        verify(esquemaRepository, never()).save(any());
    }

    @ParameterizedTest
    @CsvSource({
            "true,  true,  false, false",
            "true,  false, true,  false",
            "false, true,  false, true",
            "true,  true,  true,  true"
    })
    void deveLancarExcecaoQuandoMaisDeUmEsquemaForSelecionado(boolean e442, boolean e352,
                                                              boolean e541, boolean e244) {

        usuarioEClubeExistem();

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> service.definirEsquema(CLUBE_ID, EMAIL,
                        new EsquemaTaticoRequestDto(e442, e352, e541, e244)));

        assertEquals(KeyMessages.VALIDADE_DE_ESQUEMA, ex.getMessage());
        verify(esquemaRepository, never()).save(any());
    }

    @Test
    void deveLancarNotFoundQuandoUsuarioNaoExiste() {

        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.definirEsquema(CLUBE_ID, EMAIL,
                        new EsquemaTaticoRequestDto(true, false, false, false)));

        assertEquals("Usuário não encontrado", ex.getMessage());
        verify(esquemaRepository, never()).save(any());
    }

    @Test
    void deveLancarNotFoundQuandoClubeNaoExiste() {

        when(usuarioRepository.findByEmail(EMAIL)).thenReturn(Optional.of(usuario));
        when(clubeRepository.findById(CLUBE_ID)).thenReturn(Optional.empty());

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> service.definirEsquema(CLUBE_ID, EMAIL,
                        new EsquemaTaticoRequestDto(true, false, false, false)));

        assertEquals(KeyMessages.CLUBE_NAO_ENCONTRADO, ex.getMessage());
        verify(esquemaRepository, never()).save(any());
    }
}
