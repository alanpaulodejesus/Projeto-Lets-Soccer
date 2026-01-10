package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.entities.ClubeEntities;
import com.letssoccer.letssoccer.entities.UsuarioClubeAtivoEntity;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.UsuarioClubeAtivoRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UsuarioClubeService {

    private final UsuarioClubeAtivoRepository repository;
    private final ClubeRepository clubeRepository;

    public UsuarioClubeService(
            UsuarioClubeAtivoRepository repository,
            ClubeRepository clubeRepository) {
        this.repository = repository;
        this.clubeRepository = clubeRepository;
    }

    public void selecionarClube(Long usuarioId, Integer clubeId) {

        ClubeEntities clube = clubeRepository.findById(clubeId)
                .orElseThrow(() ->
                        new NotFoundException("Clube não encontrado"));

        UsuarioClubeAtivoEntity ativo = repository
                .findByUsuarioId(usuarioId)
                .orElse(new UsuarioClubeAtivoEntity());

        ativo.setUsuarioId(usuarioId);
        ativo.setClube(clube);
        ativo.setDataSelecao(LocalDateTime.now());

        repository.save(ativo);
    }
}
