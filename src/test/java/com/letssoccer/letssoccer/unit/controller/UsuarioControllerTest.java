package com.letssoccer.letssoccer.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letssoccer.letssoccer.controller.UsuarioController;
import com.letssoccer.letssoccer.dto.UsuarioCadastroDto;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.security.JwtAuthenticationFilter;
import com.letssoccer.letssoccer.service.JwtService;
import com.letssoccer.letssoccer.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService service;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveCadastrarUsuarioComSucesso() throws Exception {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "usuario",
                "usuario@email.com",
                "123",
                "123"
        );

        doNothing().when(service).cadastrar(dto);

        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensagem")
                        .value("Usuário cadastrado com sucesso"));
    }

    @Test
    void deveRetornar400QuandoEmailJaExiste() throws Exception {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "usuario",
                "usuario@email.com",
                "123",
                "123"
        );

        doThrow(new BadRequestException("E-mail já cadastrado"))
                .when(service).cadastrar(dto);

        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("E-mail já cadastrado"));
    }

    @Test
    void deveRetornar400QuandoSenhasNaoConferem() throws Exception {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "usuario",
                "usuario@email.com",
                "123",
                "456"
        );

        doThrow(new BadRequestException("Senhas não conferem"))
                .when(service).cadastrar(dto);

        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("Senhas não conferem"));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deveRetornar400QuandoEmailInvalido(String emailInvalido) throws Exception {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                "Alan",
                emailInvalido,
                "123",
                "123"
        );

        doThrow(new BadRequestException("Email é obrigatório"))
                .when(service).cadastrar(any());

        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("Email é obrigatório"));
    }
    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deveRetornar400QuandoNomeInvalido(String nomeInvalido) throws Exception {

        UsuarioCadastroDto dto = new UsuarioCadastroDto(
                nomeInvalido,
                "user@email.com",
                "123",
                "123"
        );

        doThrow(new BadRequestException("Nome é obrigatório"))
                .when(service).cadastrar(any());

        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensagem")
                        .value("Nome é obrigatório"));
    }
    @Test
    void deveRetornar415QuandoContentTypeForInvalido() throws Exception {

        mockMvc.perform(post("/usuarios/cadastro")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("qualquer coisa"))
                .andExpect(status().isUnsupportedMediaType());
    }
}