package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.dto.JogadorRequestDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntities;
import com.letssoccer.letssoccer.entities.JogadorEntity;
import com.letssoccer.letssoccer.mappers.JogadorMapper;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.JogadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JogadorService {

    @Autowired
    private JogadorRepository jogadorRepository;

    @Autowired
    private ClubeRepository clubeRepository;

    public JogadorResponseDto adicionarJogador(Integer clubeId, JogadorRequestDto dto) {

        ClubeEntities clube = clubeRepository.findById(clubeId)
                .orElseThrow(() -> new NotFoundException(KeyMessages.JOGADOR_NAO_ENCONTRADO));

        JogadorEntity jogador = JogadorMapper.toEntity(dto, clube);

        JogadorEntity salvo = jogadorRepository.save(jogador);

        return JogadorMapper.toDto(salvo);
    }

    public List<JogadorResponseDto> listarJogadoresDoClube(Integer clubeId) {

        if (!clubeRepository.existsById(clubeId)) {
            throw new NotFoundException(KeyMessages.JOGADOR_NAO_ENCONTRADO);
        }

        return jogadorRepository.findByClubeId(clubeId)
                .stream()
                .map(JogadorMapper::toDto)
                .toList();
    }
}
