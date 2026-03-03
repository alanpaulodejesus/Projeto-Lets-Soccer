package com.letssoccer.letssoccer.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letssoccer.letssoccer.controller.LoginController;
import com.letssoccer.letssoccer.dto.LoginRequestDto;
import com.letssoccer.letssoccer.messages.exception.UnauthorizedException;
import com.letssoccer.letssoccer.security.JwtAuthenticationFilter;
import com.letssoccer.letssoccer.service.AuthService;
import com.letssoccer.letssoccer.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false)
class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deveRealizarLoginComSucesso() throws Exception {

        LoginRequestDto dto = new LoginRequestDto(
                "usuario@email.com",
                "123"
        );

        when(authService.login(any())).thenReturn("token-fake-123");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token")
                        .value("token-fake-123"));
    }

    @Test
    void deveRetornar401QuandoSenhaInvalidas() throws Exception {

        LoginRequestDto dto = new LoginRequestDto(
                "usuario@email.com",
                "senhaErrada"
        );

        doThrow(new UnauthorizedException("Credenciais inválidas"))
                .when(authService).login(any());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem")
                        .value("Credenciais inválidas"));
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void deveRetornar401QuandoEmailInvalido(String emailInvalido) throws Exception {

        LoginRequestDto dto = new LoginRequestDto(
                emailInvalido,
                "123"
        );

        doThrow(new UnauthorizedException("Email ou senha inválidos"))
                .when(authService).login(any());

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem")
                        .value("Email ou senha inválidos"));
    }

    @Test
    void deveRetornar415QuandoContentTypeForInvalido() throws Exception {

        mockMvc.perform(post("/login")
                        .contentType(MediaType.TEXT_PLAIN)
                        .content("qualquer coisa"))
                .andExpect(status().isUnsupportedMediaType());
    }
}
