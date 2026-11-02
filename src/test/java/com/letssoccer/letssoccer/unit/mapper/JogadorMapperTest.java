package com.letssoccer.letssoccer.unit.mapper;

import com.letssoccer.letssoccer.dto.JogadorRequestDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.JogadorEntity;
import com.letssoccer.letssoccer.mappers.JogadorMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JogadorMapperTest {

    @Test
    void deveConverterRequestDtoParaEntityVinculandoOClube() {

        ClubeEntity clube = new ClubeEntity();
        clube.setId(1);

        JogadorEntity entity = JogadorMapper.toEntity(
                new JogadorRequestDto("Cássio", "Goleiro"), clube);

        assertNull(entity.getId());
        assertEquals("Cássio", entity.getNome());
        assertEquals("Goleiro", entity.getPosicao());
        assertSame(clube, entity.getClube());
    }

    @Test
    void deveConverterEntityParaResponseDto() {

        JogadorEntity jogador = new JogadorEntity();
        jogador.setId(10);
        jogador.setNome("Cássio");
        jogador.setPosicao("Goleiro");
        jogador.setFoto("/img/jogadores/cruzeiro/cassio.png");

        JogadorResponseDto dto = JogadorMapper.toDto(jogador);

        assertEquals(10, dto.id());
        assertEquals("Cássio", dto.nome());
        assertEquals("Goleiro", dto.posicao());
        assertEquals("/img/jogadores/cruzeiro/cassio.png", dto.foto());
    }

    @Test
    void deveConverterJogadorSemFotoMantendoFotoNula() {

        JogadorEntity jogador = new JogadorEntity();
        jogador.setId(11);
        jogador.setNome("Fagner");
        jogador.setPosicao("Lateral");

        assertNull(JogadorMapper.toDto(jogador).foto());
    }
}
