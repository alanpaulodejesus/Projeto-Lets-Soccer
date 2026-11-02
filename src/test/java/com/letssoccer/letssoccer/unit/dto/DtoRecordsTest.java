package com.letssoccer.letssoccer.unit.dto;

import com.letssoccer.letssoccer.dto.ClubeDetalhadoResponseDto;
import com.letssoccer.letssoccer.dto.ClubeResponseDto;
import com.letssoccer.letssoccer.dto.EscalacaoResponseDto;
import com.letssoccer.letssoccer.dto.EsquemaTaticoRequestDto;
import com.letssoccer.letssoccer.dto.EsquemaTaticoResponseDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.dto.LoginRequestDto;
import com.letssoccer.letssoccer.dto.LoginResponseDto;
import com.letssoccer.letssoccer.dto.UsuarioCadastroDto;
import com.letssoccer.letssoccer.dto.UsuarioClubeResponseDto;
import com.letssoccer.letssoccer.messages.sucess.MessageResponseDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoRecordsTest {

    @Test
    void jogadorResponseDeveExporCamposEImplementarIgualdade() {

        JogadorResponseDto a = new JogadorResponseDto(10, "Cássio", "Goleiro", "cassio.png");
        JogadorResponseDto b = new JogadorResponseDto(10, "Cássio", "Goleiro", "cassio.png");

        assertEquals(10, a.id());
        assertEquals("Cássio", a.nome());
        assertEquals("Goleiro", a.posicao());
        assertEquals("cassio.png", a.foto());
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, new JogadorResponseDto(11, "Fagner", "Lateral", null));
        assertTrue(a.toString().contains("Cássio"));
    }

    @Test
    void clubeResponseDeveExporCamposEImplementarIgualdade() {

        ClubeResponseDto a = new ClubeResponseDto(1, "Cruzeiro", "Info");

        assertEquals(1, a.id());
        assertEquals("Cruzeiro", a.nome());
        assertEquals("Info", a.informacao());
        assertEquals(a, new ClubeResponseDto(1, "Cruzeiro", "Info"));
        assertEquals(a.hashCode(), new ClubeResponseDto(1, "Cruzeiro", "Info").hashCode());
        assertNotEquals(a, new ClubeResponseDto(2, "Atlético", "Info"));
        assertTrue(a.toString().contains("Cruzeiro"));
    }

    @Test
    void clubeDetalhadoResponseDeveExporCampos() {

        JogadorResponseDto jogador = new JogadorResponseDto(10, "Cássio", "Goleiro", null);
        ClubeDetalhadoResponseDto a = new ClubeDetalhadoResponseDto(1, "Cruzeiro", "Info", List.of(jogador));

        assertEquals(1, a.id());
        assertEquals("Cruzeiro", a.nome());
        assertEquals("Info", a.informacao());
        assertEquals(List.of(jogador), a.jogadores());
        assertEquals(a, new ClubeDetalhadoResponseDto(1, "Cruzeiro", "Info", List.of(jogador)));
        assertEquals(a.hashCode(),
                new ClubeDetalhadoResponseDto(1, "Cruzeiro", "Info", List.of(jogador)).hashCode());
        assertTrue(a.toString().contains("Cruzeiro"));
    }

    @Test
    void escalacaoResponseDeveExporCamposEImplementarIgualdade() {

        JogadorResponseDto jogador = new JogadorResponseDto(10, "Cássio", "Goleiro", null);
        EscalacaoResponseDto a = new EscalacaoResponseDto("Escalação encontrada", "4-4-2", List.of(jogador));

        assertEquals("Escalação encontrada", a.mensagem());
        assertEquals("4-4-2", a.esquema());
        assertEquals(List.of(jogador), a.jogadores());
        assertEquals(a, new EscalacaoResponseDto("Escalação encontrada", "4-4-2", List.of(jogador)));
        assertEquals(a.hashCode(),
                new EscalacaoResponseDto("Escalação encontrada", "4-4-2", List.of(jogador)).hashCode());
        assertTrue(a.toString().contains("4-4-2"));
    }

    @Test
    void esquemaTaticoRecordsDevemExporCamposEImplementarIgualdade() {

        EsquemaTaticoRequestDto request = new EsquemaTaticoRequestDto(true, false, true, false);

        assertTrue(request.esquema442());
        assertFalse(request.esquema352());
        assertTrue(request.esquema541());
        assertFalse(request.esquema244());
        assertEquals(request, new EsquemaTaticoRequestDto(true, false, true, false));
        assertEquals(request.hashCode(), new EsquemaTaticoRequestDto(true, false, true, false).hashCode());
        assertNotNull(request.toString());

        EsquemaTaticoResponseDto response = new EsquemaTaticoResponseDto("ok", "4-4-2");

        assertEquals("ok", response.message());
        assertEquals("4-4-2", response.esquema());
        assertEquals(response, new EsquemaTaticoResponseDto("ok", "4-4-2"));
        assertEquals(response.hashCode(), new EsquemaTaticoResponseDto("ok", "4-4-2").hashCode());
        assertNotNull(response.toString());
    }

    @Test
    void loginRecordsDevemExporCamposEImplementarIgualdade() {

        LoginRequestDto request = new LoginRequestDto("usuario@email.com", "123");

        assertEquals("usuario@email.com", request.email());
        assertEquals("123", request.senha());
        assertEquals(request, new LoginRequestDto("usuario@email.com", "123"));
        assertEquals(request.hashCode(), new LoginRequestDto("usuario@email.com", "123").hashCode());
        assertNotNull(request.toString());

        LoginResponseDto response = new LoginResponseDto("token");

        assertEquals("token", response.token());
        assertEquals(response, new LoginResponseDto("token"));
        assertEquals(response.hashCode(), new LoginResponseDto("token").hashCode());
        assertNotNull(response.toString());
    }

    @Test
    void usuarioCadastroDeveExporCamposEImplementarIgualdade() {

        UsuarioCadastroDto a = new UsuarioCadastroDto("Alan", "alan@email.com", "123", "123");

        assertEquals("Alan", a.nome());
        assertEquals("alan@email.com", a.email());
        assertEquals("123", a.senha());
        assertEquals("123", a.confirmacaoSenha());
        assertEquals(a, new UsuarioCadastroDto("Alan", "alan@email.com", "123", "123"));
        assertEquals(a.hashCode(),
                new UsuarioCadastroDto("Alan", "alan@email.com", "123", "123").hashCode());
        assertNotNull(a.toString());
    }

    @Test
    void usuarioClubeResponseEMessageResponseDevemExporCampos() {

        UsuarioClubeResponseDto clube = new UsuarioClubeResponseDto(10, "Cruzeiro");

        assertEquals(10, clube.id());
        assertEquals("Cruzeiro", clube.nome());
        assertEquals(clube, new UsuarioClubeResponseDto(10, "Cruzeiro"));
        assertEquals(clube.hashCode(), new UsuarioClubeResponseDto(10, "Cruzeiro").hashCode());
        assertNotNull(clube.toString());

        MessageResponseDto mensagem = new MessageResponseDto("ok");

        assertEquals("ok", mensagem.mensagem());
        assertEquals(mensagem, new MessageResponseDto("ok"));
        assertEquals(mensagem.hashCode(), new MessageResponseDto("ok").hashCode());
        assertNotNull(mensagem.toString());
    }
}
