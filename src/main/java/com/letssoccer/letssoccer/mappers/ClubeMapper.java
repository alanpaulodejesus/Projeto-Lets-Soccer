package com.letssoccer.letssoccer.mappers;

import com.letssoccer.letssoccer.dto.ClubeDetalhadoResponseDto;
import com.letssoccer.letssoccer.dto.ClubeRequestDto;
import com.letssoccer.letssoccer.dto.ClubeResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;

public class ClubeMapper {

    public static ClubeEntity toClubeEntity(ClubeRequestDto dto){
        ClubeEntity clubeEntity = new ClubeEntity();
        clubeEntity.setNome(dto.nome());
        clubeEntity.setInformacao(dto.informacao());
        return clubeEntity;
    }

    public static ClubeResponseDto toClubeResponseDTO(ClubeEntity clubeEntity) {
        return new ClubeResponseDto(
                clubeEntity.getId(),
                clubeEntity.getNome(),
                clubeEntity.getInformacao()
        );
    }

    public static ClubeDetalhadoResponseDto toDetalhadoDto(ClubeEntity entity) {
        return new ClubeDetalhadoResponseDto(
                entity.getId(),
                entity.getNome(),
                entity.getInformacao(),
                entity.getJogadores()
                        .stream()
                        .map(JogadorMapper::toDto)
                        .toList()
        );
    }
}
