package com.letssoccer.letssoccer.mappers;

import com.letssoccer.letssoccer.dto.ClubeRequestDto;
import com.letssoccer.letssoccer.dto.ClubeResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntities;

public class ClubeMapper {

    public static ClubeEntities toClubeEntity(ClubeRequestDto dto){
        ClubeEntities clubeEntities = new ClubeEntities();
        clubeEntities.setNome(dto.nome());
        clubeEntities.setInformacao(dto.informacao());
        return clubeEntities;
    }

    public static ClubeResponseDto toClubeResponseDTO(ClubeEntities clubeEntities) {
        return new ClubeResponseDto(
                clubeEntities.getId(),
                clubeEntities.getNome(),
                clubeEntities.getInformacao()
        );
    }
}
