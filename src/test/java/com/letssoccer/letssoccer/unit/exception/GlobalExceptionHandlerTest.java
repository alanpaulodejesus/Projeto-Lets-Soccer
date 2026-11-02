package com.letssoccer.letssoccer.unit.exception;

import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.GlobalExceptionHandler;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.messages.exception.UnauthorizedException;
import com.letssoccer.letssoccer.messages.sucess.MessageResponseDto;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    // usado apenas para obter um MethodParameter válido
    @SuppressWarnings("unused")
    private void metodoQualquer(String parametro) {
    }

    @Test
    void deveRetornar400ParaBadRequestException() {

        ResponseEntity<MessageResponseDto> response =
                handler.handleBadRequest(new BadRequestException("Requisição inválida"));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Requisição inválida", response.getBody().mensagem());
    }

    @Test
    void deveRetornar404ParaNotFoundException() {

        ResponseEntity<MessageResponseDto> response =
                handler.handleNotFoundRequest(new NotFoundException("Clube não encontrado"));

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Clube não encontrado", response.getBody().mensagem());
    }

    @Test
    void deveRetornar401ParaUnauthorizedException() {

        ResponseEntity<MessageResponseDto> response =
                handler.handleUnauthorized(new UnauthorizedException("Email ou senha inválidos"));

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Email ou senha inválidos", response.getBody().mensagem());
    }

    @Test
    void deveRetornar400ComMapaDeCamposParaErrosDeValidacao() throws Exception {

        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "clubeRequestDto");
        bindingResult.addError(new FieldError("clubeRequestDto", "nome", "O nome do clube é obrigatório"));
        bindingResult.addError(new FieldError("clubeRequestDto", "informacao", "A informação do clube é obrigatório"));

        MethodParameter parametro = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("metodoQualquer", String.class), 0);

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(
                new MethodArgumentNotValidException(parametro, bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().size());
        assertEquals("O nome do clube é obrigatório", response.getBody().get("nome"));
        assertEquals("A informação do clube é obrigatório", response.getBody().get("informacao"));
    }

    @Test
    void deveRetornarMapaVazioQuandoNaoHaErrosDeCampo() throws Exception {

        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "objeto");

        MethodParameter parametro = new MethodParameter(
                GlobalExceptionHandlerTest.class.getDeclaredMethod("metodoQualquer", String.class), 0);

        ResponseEntity<Map<String, String>> response = handler.handleValidationErrors(
                new MethodArgumentNotValidException(parametro, bindingResult));

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
}
