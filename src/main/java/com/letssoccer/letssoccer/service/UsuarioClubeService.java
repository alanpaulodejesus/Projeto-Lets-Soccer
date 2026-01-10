package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UsuarioClubeService {

    private final UsuarioRepository usuarioRepository;
    private final ClubeRepository clubeRepository;

    public UsuarioClubeService(
            UsuarioRepository usuarioRepository,
            ClubeRepository clubeRepository) {
        this.usuarioRepository = usuarioRepository;
        this.clubeRepository = clubeRepository;
    }

    public void selecionarClube(Long usuarioId, Integer clubeId) {

        UsuarioEntity usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        ClubeEntity clube = clubeRepository.findById(clubeId)
                .orElseThrow(() -> new RuntimeException("Clube não encontrado"));

        usuario.setClube(clube);
        usuarioRepository.save(usuario);
    }
}