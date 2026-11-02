package com.letssoccer.letssoccer.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letssoccer.letssoccer.controller.ClubeController;
import com.letssoccer.letssoccer.dto.ClubeDetalhadoResponseDto;
import com.letssoccer.letssoccer.dto.ClubeRequestDto;
import com.letssoccer.letssoccer.dto.ClubeResponseDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.messages.sucess.SuccessMessages;
import com.letssoccer.letssoccer.security.JwtAuthenticationFilter;
import com.letssoccer.letssoccer.service.ClubeService;
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

@WebMvcTest(ClubeController.class)
@AutoConfigureMockMvc(addFilters = false)
class ClubeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ClubeService clubeService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    // ===== POST /clube =====

    @Test
    void deveCriarClubeComSucesso() throws Exception {

        ClubeRequestDto dto = new ClubeRequestDto("Cruzeiro", "Fundado em 1921");

        when(clubeService.criarClube(any()))
                .thenReturn(new ClubeResponseDto(1, "Cruzeiro", "Fundado em 1921"));

        mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Cruzeiro"))
                .andExpect(jsonPath("$.informacao").value("Fundado em 1921"));
    }

    @Test
    void deveRetornar400QuandoNomeDoClubeEstiverEmBranco() throws Exception {

        ClubeRequestDto dto = new ClubeRequestDto("", "Fundado em 1921");

        mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").value(KeyMessages.NOME_CLUBE_OBRIGATORIO));

        verify(clubeService, never()).criarClube(any());
    }

    @Test
    void deveRetornar400QuandoInformacaoDoClubeEstiverEmBranco() throws Exception {

        ClubeRequestDto dto = new ClubeRequestDto("Cruzeiro", "   ");

        mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.informacao").value(KeyMessages.INFORMACAO_CLUBE_OBRIGATORIO));

        verify(clubeService, never()).criarClube(any());
    }

    @Test
    void deveRetornar400QuandoClubeJaCadastrado() throws Exception {

        ClubeRequestDto dto = new ClubeRequestDto("Cruzeiro", "Fundado em 1921");

        when(clubeService.criarClube(any()))
                .thenThrow(new BadRequestException(KeyMessages.NOME_CLUBE_CADASTRADO));

        mockMvc.perform(post("/clube")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem").value(KeyMessages.NOME_CLUBE_CADASTRADO));
    }

    // ===== GET /clube =====

    @Test
    void deveListarClubes() throws Exception {

        when(clubeService.listarClubes()).thenReturn(List.of(
                new ClubeResponseDto(1, "Cruzeiro", "Info 1"),
                new ClubeResponseDto(2, "Atlético", "Info 2")
        ));

        mockMvc.perform(get("/clube"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Cruzeiro"))
                .andExpect(jsonPath("$[1].nome").value("Atlético"));
    }

    @Test
    void deveListarClubesVazio() throws Exception {

        when(clubeService.listarClubes()).thenReturn(List.of());

        mockMvc.perform(get("/clube"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // ===== GET /clube/{id} =====

    @Test
    void deveBuscarClubePorId() throws Exception {

        when(clubeService.buscarClubePorId(1))
                .thenReturn(new ClubeResponseDto(1, "Cruzeiro", "Info"));

        mockMvc.perform(get("/clube/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Cruzeiro"));
    }

    @Test
    void deveRetornar404QuandoClubeNaoExisteAoBuscarPorId() throws Exception {

        when(clubeService.buscarClubePorId(99))
                .thenThrow(new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));

        mockMvc.perform(get("/clube/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value(KeyMessages.CLUBE_NAO_ENCONTRADO));
    }

    // ===== PUT /clube/{id} =====

    @Test
    void deveAtualizarClube() throws Exception {

        ClubeRequestDto dto = new ClubeRequestDto("Cruzeiro EC", "Info nova");

        when(clubeService.atualizarClube(eq(1), any()))
                .thenReturn(new ClubeResponseDto(1, "Cruzeiro EC", "Info nova"));

        mockMvc.perform(put("/clube/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cruzeiro EC"))
                .andExpect(jsonPath("$.informacao").value("Info nova"));
    }

    @Test
    void deveRetornar400QuandoAtualizarComNomeEmBranco() throws Exception {

        ClubeRequestDto dto = new ClubeRequestDto("", "Info nova");

        mockMvc.perform(put("/clube/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.nome").value(KeyMessages.NOME_CLUBE_OBRIGATORIO));

        verify(clubeService, never()).atualizarClube(any(), any());
    }

    @Test
    void deveRetornar404QuandoAtualizarClubeInexistente() throws Exception {

        ClubeRequestDto dto = new ClubeRequestDto("Nome", "Info");

        when(clubeService.atualizarClube(eq(99), any()))
                .thenThrow(new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));

        mockMvc.perform(put("/clube/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value(KeyMessages.CLUBE_NAO_ENCONTRADO));
    }

    // ===== DELETE /clube/{id} =====

    @Test
    void deveDeletarClube() throws Exception {

        doNothing().when(clubeService).deletarClube(1);

        mockMvc.perform(delete("/clube/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value(SuccessMessages.CLUBE_EXCLUIDO));

        verify(clubeService).deletarClube(1);
    }

    @Test
    void deveRetornar400AoDeletarClubeComJogadores() throws Exception {

        doThrow(new BadRequestException(KeyMessages.NAO_PERMITE_EXCLUSAO_CLUBE_COM_JOGADOR))
                .when(clubeService).deletarClube(1);

        mockMvc.perform(delete("/clube/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value(KeyMessages.NAO_PERMITE_EXCLUSAO_CLUBE_COM_JOGADOR));
    }

    @Test
    void deveRetornar404AoDeletarClubeInexistente() throws Exception {

        doThrow(new NotFoundException("Clube com ID 99 não encontrado."))
                .when(clubeService).deletarClube(99);

        mockMvc.perform(delete("/clube/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value("Clube com ID 99 não encontrado."));
    }

    // ===== DELETE /clube =====

    @Test
    void deveDeletarTodosOsClubes() throws Exception {

        doNothing().when(clubeService).deletarClubes();

        mockMvc.perform(delete("/clube"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem").value(SuccessMessages.CLUBES_EXCLUIDOS));

        verify(clubeService).deletarClubes();
    }

    @Test
    void deveRetornar400AoDeletarTodosOsClubesComJogadores() throws Exception {

        doThrow(new BadRequestException(KeyMessages.NAO_PERMITE_EXCLUSAO_CLUBE_COM_JOGADOR))
                .when(clubeService).deletarClubes();

        mockMvc.perform(delete("/clube"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value(KeyMessages.NAO_PERMITE_EXCLUSAO_CLUBE_COM_JOGADOR));
    }

    // ===== GET /clube/{id}/detalhado =====

    @Test
    void deveBuscarClubeDetalhado() throws Exception {

        when(clubeService.buscarClubeDetalhado(1)).thenReturn(
                new ClubeDetalhadoResponseDto(1, "Cruzeiro", "Info", List.of(
                        new JogadorResponseDto(10, "Cássio", "Goleiro", "cassio.png")
                ))
        );

        mockMvc.perform(get("/clube/1/detalhado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Cruzeiro"))
                .andExpect(jsonPath("$.jogadores.length()").value(1))
                .andExpect(jsonPath("$.jogadores[0].nome").value("Cássio"))
                .andExpect(jsonPath("$.jogadores[0].posicao").value("Goleiro"));
    }

    @Test
    void deveRetornar404AoBuscarDetalhadoDeClubeInexistente() throws Exception {

        when(clubeService.buscarClubeDetalhado(99))
                .thenThrow(new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));

        mockMvc.perform(get("/clube/99/detalhado"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensagem").value(KeyMessages.CLUBE_NAO_ENCONTRADO));
    }
}
