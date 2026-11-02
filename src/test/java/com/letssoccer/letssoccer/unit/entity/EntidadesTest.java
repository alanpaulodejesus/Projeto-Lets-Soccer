package com.letssoccer.letssoccer.unit.entity;

import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.EscalacaoEntity;
import com.letssoccer.letssoccer.entities.EsquemaTaticoEntity;
import com.letssoccer.letssoccer.entities.JogadorEntity;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EntidadesTest {

    // ===== ClubeEntity =====

    @Test
    void clubeDeveExporOsDadosEIniciarComListaDeJogadoresVazia() {

        ClubeEntity clube = new ClubeEntity();

        assertNotNull(clube.getJogadores());
        assertTrue(clube.getJogadores().isEmpty());

        clube.setId(1);
        clube.setNome("Cruzeiro");
        clube.setInformacao("Fundado em 1921");

        assertEquals(1, clube.getId());
        assertEquals("Cruzeiro", clube.getNome());
        assertEquals("Fundado em 1921", clube.getInformacao());
    }

    @Test
    void clubeDeveGuardarListaDeJogadores() {

        ClubeEntity clube = new ClubeEntity();
        JogadorEntity jogador = new JogadorEntity();
        jogador.setId(10);

        clube.setJogadores(List.of(jogador));

        assertEquals(1, clube.getJogadores().size());
        assertSame(jogador, clube.getJogadores().get(0));
    }

    // ===== JogadorEntity =====

    @Test
    void jogadorDeveExporOsDados() {

        ClubeEntity clube = new ClubeEntity();
        JogadorEntity jogador = new JogadorEntity();

        jogador.setId(10);
        jogador.setNome("Cássio");
        jogador.setPosicao("Goleiro");
        jogador.setFoto("/img/cassio.png");
        jogador.setClube(clube);

        assertEquals(10, jogador.getId());
        assertEquals("Cássio", jogador.getNome());
        assertEquals("Goleiro", jogador.getPosicao());
        assertEquals("/img/cassio.png", jogador.getFoto());
        assertSame(clube, jogador.getClube());
    }

    // ===== EsquemaTaticoEntity =====

    @Test
    void esquemaTaticoDeveExporOsDados() {

        ClubeEntity clube = new ClubeEntity();
        UsuarioEntity usuario = new UsuarioEntity();
        EsquemaTaticoEntity esquema = new EsquemaTaticoEntity();

        assertFalse(esquema.isEsquema442());
        assertFalse(esquema.isEsquema352());
        assertFalse(esquema.isEsquema541());
        assertFalse(esquema.isEsquema244());

        esquema.setId(5);
        esquema.setEsquema442(true);
        esquema.setEsquema352(true);
        esquema.setEsquema541(true);
        esquema.setEsquema244(true);
        esquema.setClube(clube);
        esquema.setUsuario(usuario);

        assertEquals(5, esquema.getId());
        assertTrue(esquema.isEsquema442());
        assertTrue(esquema.isEsquema352());
        assertTrue(esquema.isEsquema541());
        assertTrue(esquema.isEsquema244());
        assertSame(clube, esquema.getClube());
        assertSame(usuario, esquema.getUsuario());
    }

    // ===== EscalacaoEntity =====

    @Test
    void escalacaoDeveExporOsDados() {

        ClubeEntity clube = new ClubeEntity();
        UsuarioEntity usuario = new UsuarioEntity();
        JogadorEntity jogador = new JogadorEntity();
        EscalacaoEntity escalacao = new EscalacaoEntity();

        escalacao.setId(7);
        escalacao.setEsquema("4-4-2");
        escalacao.setClube(clube);
        escalacao.setUsuario(usuario);
        escalacao.setJogadores(List.of(jogador));

        assertEquals(7, escalacao.getId());
        assertEquals("4-4-2", escalacao.getEsquema());
        assertSame(clube, escalacao.getClube());
        assertSame(usuario, escalacao.getUsuario());
        assertEquals(1, escalacao.getJogadores().size());
    }

    @Test
    void escalacaoDevePreencherDataDeCriacaoAoPersistir() {

        EscalacaoEntity escalacao = new EscalacaoEntity();
        assertNull(escalacao.getDataCriacao());

        LocalDateTime antes = LocalDateTime.now().minusSeconds(1);
        escalacao.prePersist();
        LocalDateTime depois = LocalDateTime.now().plusSeconds(1);

        assertNotNull(escalacao.getDataCriacao());
        assertTrue(escalacao.getDataCriacao().isAfter(antes));
        assertTrue(escalacao.getDataCriacao().isBefore(depois));
    }
}
