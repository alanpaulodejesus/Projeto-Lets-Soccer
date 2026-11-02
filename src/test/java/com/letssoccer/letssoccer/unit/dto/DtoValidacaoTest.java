package com.letssoccer.letssoccer.unit.dto;

import com.letssoccer.letssoccer.dto.ClubeRequestDto;
import com.letssoccer.letssoccer.dto.EscalacaoRequestDto;
import com.letssoccer.letssoccer.dto.EsquemaTaticoRequestDto;
import com.letssoccer.letssoccer.dto.JogadorRequestDto;
import com.letssoccer.letssoccer.dto.UsuarioClubeRequestDto;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class DtoValidacaoTest {

    private static ValidatorFactory factory;
    private static Validator validator;

    @BeforeAll
    static void iniciarValidator() {
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @AfterAll
    static void fecharFactory() {
        factory.close();
    }

    private <T> Set<String> camposInvalidos(T dto) {
        return validator.validate(dto).stream()
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toSet());
    }

    // ===== ClubeRequestDto =====

    @Test
    void clubeRequestValidoNaoDeveTerViolacoes() {

        assertTrue(validator.validate(new ClubeRequestDto("Cruzeiro", "Fundado em 1921")).isEmpty());
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void clubeRequestDeveRejeitarNomeEmBranco(String nome) {

        Set<ConstraintViolation<ClubeRequestDto>> violacoes =
                validator.validate(new ClubeRequestDto(nome, "Info"));

        assertEquals(1, violacoes.size());
        ConstraintViolation<ClubeRequestDto> violacao = violacoes.iterator().next();
        assertEquals("nome", violacao.getPropertyPath().toString());
        assertEquals(KeyMessages.NOME_CLUBE_OBRIGATORIO, violacao.getMessage());
    }

    @NullSource
    @ParameterizedTest
    @ValueSource(strings = { "", "   " })
    void clubeRequestDeveRejeitarInformacaoEmBranco(String informacao) {

        Set<ConstraintViolation<ClubeRequestDto>> violacoes =
                validator.validate(new ClubeRequestDto("Cruzeiro", informacao));

        assertEquals(1, violacoes.size());
        ConstraintViolation<ClubeRequestDto> violacao = violacoes.iterator().next();
        assertEquals("informacao", violacao.getPropertyPath().toString());
        assertEquals(KeyMessages.INFORMACAO_CLUBE_OBRIGATORIO, violacao.getMessage());
    }

    // ===== JogadorRequestDto =====

    @Test
    void jogadorRequestValidoNaoDeveTerViolacoes() {

        assertTrue(validator.validate(new JogadorRequestDto("Cássio", "Goleiro")).isEmpty());
    }

    @Test
    void jogadorRequestDeveRejeitarNomeEPosicaoEmBranco() {

        assertEquals(Set.of("nome", "posicao"), camposInvalidos(new JogadorRequestDto("", " ")));
    }

    // ===== UsuarioClubeRequestDto =====

    @Test
    void usuarioClubeRequestDeveExigirClubeId() {

        assertTrue(validator.validate(new UsuarioClubeRequestDto(10)).isEmpty());
        assertEquals(Set.of("clubeId"), camposInvalidos(new UsuarioClubeRequestDto(null)));
    }

    // ===== EscalacaoRequestDto =====

    @Test
    void escalacaoRequestValidaNaoDeveTerViolacoes() {

        EscalacaoRequestDto dto = new EscalacaoRequestDto();
        dto.setJogadoresIds(List.of(1, 2, 3));

        assertTrue(validator.validate(dto).isEmpty());
        assertEquals(List.of(1, 2, 3), dto.getJogadoresIds());
    }

    @Test
    void escalacaoRequestDeveRejeitarListaNulaOuVazia() {

        EscalacaoRequestDto nula = new EscalacaoRequestDto();
        assertEquals(Set.of("jogadoresIds"), camposInvalidos(nula));

        EscalacaoRequestDto vazia = new EscalacaoRequestDto();
        vazia.setJogadoresIds(new ArrayList<>());
        assertEquals(Set.of("jogadoresIds"), camposInvalidos(vazia));
    }

    // ===== EsquemaTaticoRequestDto =====

    @Test
    void esquemaTaticoDeveIndicarSeAlgumEsquemaFoiSelecionado() {

        assertTrue(new EsquemaTaticoRequestDto(true, false, false, false).esquemaSelecionado());
        assertTrue(new EsquemaTaticoRequestDto(false, true, false, false).esquemaSelecionado());
        assertTrue(new EsquemaTaticoRequestDto(false, false, true, false).esquemaSelecionado());
        assertTrue(new EsquemaTaticoRequestDto(false, false, false, true).esquemaSelecionado());
        assertFalse(new EsquemaTaticoRequestDto(false, false, false, false).esquemaSelecionado());
    }
}
