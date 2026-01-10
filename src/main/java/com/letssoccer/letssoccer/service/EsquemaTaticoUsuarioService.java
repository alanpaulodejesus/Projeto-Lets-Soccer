package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.entities.EsquemaTaticoUsuarioEntity;
import com.letssoccer.letssoccer.entities.UsuarioClubeAtivoEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.repositories.EsquemaTaticoUsuarioRepository;
import com.letssoccer.letssoccer.repositories.UsuarioClubeAtivoRepository;
import org.springframework.stereotype.Service;

@Service
public class EsquemaTaticoUsuarioService {

    private final UsuarioClubeAtivoRepository clubeAtivoRepository;
    private final EsquemaTaticoUsuarioRepository esquemaRepository;

    public EsquemaTaticoUsuarioService(
            UsuarioClubeAtivoRepository clubeAtivoRepository,
            EsquemaTaticoUsuarioRepository esquemaRepository) {
        this.clubeAtivoRepository = clubeAtivoRepository;
        this.esquemaRepository = esquemaRepository;
    }

    public void definirEsquema(Long usuarioId, String esquema) {

        UsuarioClubeAtivoEntity ativo = clubeAtivoRepository
                .findByUsuarioId(usuarioId)
                .orElseThrow(() ->
                        new BadRequestException("Usuário não possui clube ativo"));

        EsquemaTaticoUsuarioEntity entity = new EsquemaTaticoUsuarioEntity();
        entity.setUsuarioId(usuarioId);
        entity.setClube(ativo.getClube());
        entity.setEsquema(esquema);

        esquemaRepository.save(entity);
    }
}
