package com.letssoccer.letssoccer.unit.mapper;

import com.letssoccer.letssoccer.dto.ClubeDetalhadoResponseDto;
import com.letssoccer.letssoccer.dto.ClubeRequestDto;
import com.letssoccer.letssoccer.dto.ClubeResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.JogadorEntity;
import com.letssoccer.letssoccer.mappers.ClubeMapper;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ClubeMapperTest {

    private ClubeEntity clube() {
        ClubeEntity clube = new ClubeEntity();
        clube.setId(1);
        clube.setNome("Cruzeiro");
        clube.setInformacao("Fundado em 1921");
        return clube;
    }

    private JogadorEntity jogador(Integer id, String nome, ClubeEntity clube) {
        JogadorEntity jogador = new JogadorEntity();
        jogador.setId(id);
        jogador.setNome(nome);
        jogador.setPosicao("Goleiro");
        jogador.setFoto(nome + ".png");
        jogador.setClube(clube);
        return jogador;
    }

    @Test
    void deveConverterRequestDtoParaEntity() {

        ClubeEntity entity = ClubeMapper.toClubeEntity(
                new ClubeRequestDto("Cruzeiro", "Fundado em 1921"));

        assertNull(entity.getId());
        assertEquals("Cruzeiro", entity.getNome());
        assertEquals("Fundado em 1921", entity.getInformacao());
        assertTrue(entity.getJogadores().isEmpty());
    }

    @Test
    void deveConverterEntityParaResponseDto() {

        ClubeResponseDto dto = ClubeMapper.toClubeResponseDTO(clube());

        assertEquals(1, dto.id());
        assertEquals("Cruzeiro", dto.nome());
        assertEquals("Fundado em 1921", dto.informacao());
    }

    @Test
    void deveConverterEntityParaDtoDetalhadoComJogadores() {

        ClubeEntity clube = clube();
        clube.setJogadores(List.of(
                jogador(10, "Cássio", clube),
                jogador(11, "Fagner", clube)
        ));

        ClubeDetalhadoResponseDto dto = ClubeMapper.toDetalhadoDto(clube);

        assertEquals(1, dto.id());
        assertEquals("Cruzeiro", dto.nome());
        assertEquals("Fundado em 1921", dto.informacao());
        assertEquals(2, dto.jogadores().size());
        assertEquals(10, dto.jogadores().get(0).id());
        assertEquals("Cássio", dto.jogadores().get(0).nome());
        assertEquals("Fagner", dto.jogadores().get(1).nome());
    }

    @Test
    void deveConverterClubeSemJogadoresParaDtoDetalhadoComListaVazia() {

        ClubeDetalhadoResponseDto dto = ClubeMapper.toDetalhadoDto(clube());

        assertTrue(dto.jogadores().isEmpty());
    }
}
