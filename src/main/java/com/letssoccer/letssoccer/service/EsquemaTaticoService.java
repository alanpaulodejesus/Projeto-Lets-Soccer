package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.dto.EsquemaTaticoRequestDto;
import com.letssoccer.letssoccer.entities.ClubeEntities;
import com.letssoccer.letssoccer.entities.EsquemaTaticoEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.EsquemaTaticoRepository;
import org.springframework.stereotype.Service;

@Service
public class EsquemaTaticoService {

    private final ClubeRepository clubeRepository;
    private final EsquemaTaticoRepository esquemaRepository;

    public EsquemaTaticoService(
            ClubeRepository clubeRepository,
            EsquemaTaticoRepository esquemaRepository) {
        this.clubeRepository = clubeRepository;
        this.esquemaRepository = esquemaRepository;
    }

    public String definirEsquema(Integer clubeId, EsquemaTaticoRequestDto dto) {

        ClubeEntities clube = clubeRepository.findById(clubeId)
                .orElseThrow(() ->
                        new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));

        String esquemaSelecionado = obterEsquemaSelecionado(dto);

        EsquemaTaticoEntity esquema = esquemaRepository
                .findByClubeId(clubeId)
                .orElse(new EsquemaTaticoEntity());

        esquema.setClube(clube);
        esquema.setEsquema442(dto.esquema442());
        esquema.setEsquema352(dto.esquema352());
        esquema.setEsquema541(dto.esquema541());
        esquema.setEsquema244(dto.esquema244());

        esquemaRepository.save(esquema);

        return esquemaSelecionado;
    }

    private String obterEsquemaSelecionado(EsquemaTaticoRequestDto dto) {
        if (dto.esquema442()) return "4-4-2";
        if (dto.esquema352()) return "3-5-2";
        if (dto.esquema541()) return "5-4-1";
        if (dto.esquema244()) return "2-4-4";
        throw new BadRequestException("Nenhum esquema tático selecionado");
    }


}
