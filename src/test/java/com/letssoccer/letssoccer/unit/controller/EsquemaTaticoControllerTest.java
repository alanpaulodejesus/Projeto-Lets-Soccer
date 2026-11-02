package com.letssoccer.letssoccer.unit.controller;

import com.letssoccer.letssoccer.controller.EsquemaTaticoController;
import com.letssoccer.letssoccer.dto.EsquemaTaticoRequestDto;
import com.letssoccer.letssoccer.dto.EsquemaTaticoResponseDto;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.service.EsquemaTaticoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Chama o controller diretamente porque o usuário logado chega via {@code @AuthenticationPrincipal}.
 */
class EsquemaTaticoControllerTest {

    private static final String EMAIL = "usuario@email.com";

    @Mock
    private EsquemaTaticoService esquemaService;

    @InjectMocks
    private EsquemaTaticoController controller;

    private UserDetails usuarioLogado;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        usuarioLogado = User.withUsername(EMAIL).password("senha").roles("USER").build();
    }

    @Test
    void deveDefinirEsquemaERetornarMensagemDeSucesso() {

        EsquemaTaticoRequestDto dto = new EsquemaTaticoRequestDto(true, false, false, false);

        when(esquemaService.definirEsquema(1, EMAIL, dto)).thenReturn("4-4-2");

        ResponseEntity<EsquemaTaticoResponseDto> response =
                controller.definirEsquema(1, dto, usuarioLogado);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Esquema tático definido com sucesso", response.getBody().message());
        assertEquals("4-4-2", response.getBody().esquema());
        verify(esquemaService).definirEsquema(1, EMAIL, dto);
    }

    @Test
    void devePropagarExcecaoQuandoNenhumEsquemaForSelecionado() {

        EsquemaTaticoRequestDto dto = new EsquemaTaticoRequestDto(false, false, false, false);

        when(esquemaService.definirEsquema(1, EMAIL, dto))
                .thenThrow(new BadRequestException(KeyMessages.OBRIGATORIEDADE_ESQUEMA));

        BadRequestException ex = assertThrows(BadRequestException.class,
                () -> controller.definirEsquema(1, dto, usuarioLogado));

        assertEquals(KeyMessages.OBRIGATORIEDADE_ESQUEMA, ex.getMessage());
    }
}
