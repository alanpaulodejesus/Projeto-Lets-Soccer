package com.letssoccer.letssoccer.mappers;

import com.letssoccer.letssoccer.dto.JogadorRequestDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.JogadorEntity;

public class JogadorMapper {

    public static JogadorEntity toEntity(JogadorRequestDto dto, ClubeEntity clube) {
        JogadorEntity jogador = new JogadorEntity();
        jogador.setNome(dto.nome());
        jogador.setPosicao(dto.posicao());
        jogador.setClube(clube);
        return jogador;
    }

    public static JogadorResponseDto toDto(JogadorEntity jogador) {
        return new JogadorResponseDto(
                jogador.getId(),
                jogador.getNome(),
                jogador.getPosicao(),
                jogador.getFoto()
        );
    }


}

