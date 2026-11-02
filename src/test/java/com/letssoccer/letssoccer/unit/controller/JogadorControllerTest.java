package com.letssoccer.letssoccer.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letssoccer.letssoccer.controller.JogadorController;
import com.letssoccer.letssoccer.dto.JogadorRequestDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.messages.sucess.SuccessMessages;
import com.letssoccer.letssoccer.security.JwtAuthenticationFilter;
import com.letssoccer.letssoccer.service.JogadorService;
import com.letssoccer.letssoccer.service.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(JogadorController.class)
@AutoConfigureMockMvc(addFilters = false)
class JogadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JogadorService jogadorService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    // ===== POST /clube/{clubeId}/jogador =====

    @Test
    void deveAdicionarJogadorComSucesso() throws Exception {

        JogadorRequestDto dto = new JogadorRequestDto("Cássio", "Goleiro");

        when(jogadorService.adicionarJogador(eq(1), any()))
                .thenReturn(new JogadorResponseDto(10, "Cássio", "Goleiro", null));

        mockMvc.perform(post("/clube/1/jogador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nome").value("Cássio"))
                .andExpect(jsonPath("$.posicao").value("Goleiro"));
    }

    @Test
    void deveRetornar400QuandoNomeDoJogadorEstiverEmBranco() throws Exception {

        JogadorRequestDto dto = new JogadorRequestDto("", "Goleiro");

        mockMvc.perform(post("/clube/1/jogador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").exists());

        verify(jogadorService, never()).adicionarJogador(any(), any());
    }

    @Test
    void deveRetornar400QuandoPosicaoDoJogadorEstiverEmBranco() throws Exception {

        JogadorRequestDto dto = new JogadorRequestDto("Cássio", " ");

        mockMvc.perform(post("/clube/1/jogador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.posicao").exists());

        verify(jogadorService, never()).adicionarJogador(any(), any());
    }

    @Test
    void deveRetornar404QuandoClubeNaoExisteAoAdicionarJogador() throws Exception {

        JogadorRequestDto dto = new JogadorRequestDto("Cássio", "Goleiro");

        when(jogadorService.adicionarJogador(eq(99), any()))
                .thenThrow(new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));

        mockMvc.perform(post("/clube/99/jogador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value(KeyMessages.CLUBE_NAO_ENCONTRADO));
    }

    @Test
    void deveRetornar400QuandoJogadorJaExisteNoClube() throws Exception {

        JogadorRequestDto dto = new JogadorRequestDto("Cássio", "Goleiro");

        when(jogadorService.adicionarJogador(eq(1), any()))
                .thenThrow(new BadRequestException(KeyMessages.DUPLICIDADE_NOME_JOGADOR));

        mockMvc.perform(post("/clube/1/jogador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(KeyMessages.DUPLICIDADE_NOME_JOGADOR));
    }

    // ===== GET /clube/{clubeId}/jogador =====

    @Test
    void deveListarJogadoresDoClube() throws Exception {

        when(jogadorService.listarJogadoresDoClube(1)).thenReturn(List.of(
                new JogadorResponseDto(10, "Cássio", "Goleiro", "cassio.png"),
                new JogadorResponseDto(11, "Fagner", "Lateral", "fagner.png")
        ));

        mockMvc.perform(get("/clube/1/jogador"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Cássio"))
                .andExpect(jsonPath("$[1].foto").value("fagner.png"));
    }

    @Test
    void deveRetornar404AoListarJogadoresDeClubeInexistente() throws Exception {

        when(jogadorService.listarJogadoresDoClube(99))
                .thenThrow(new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));

        mockMvc.perform(get("/clube/99/jogador"))
                .andExpect(status().isNotFound());
    }

    // ===== DELETE /clube/{clubeId}/jogador/{id} =====

    @Test
    void deveDeletarJogador() throws Exception {

        doNothing().when(jogadorService).deletarJogador(10);

        mockMvc.perform(delete("/clube/1/jogador/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value(SuccessMessages.JOGADOR_EXCLUIDO));

        verify(jogadorService).deletarJogador(10);
    }

    @Test
    void deveRetornar404AoDeletarJogadorInexistente() throws Exception {

        doThrow(new NotFoundException("Jogador com ID 99 não encontrado."))
                .when(jogadorService).deletarJogador(99);

        mockMvc.perform(delete("/clube/1/jogador/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Jogador com ID 99 não encontrado."));
    }
}
