package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.dto.ClubeDetalhadoResponseDto;
import com.letssoccer.letssoccer.dto.ClubeRequestDto;
import com.letssoccer.letssoccer.dto.ClubeResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntities;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.mappers.ClubeMapper;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.JogadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubeService {
    @Autowired
    private ClubeRepository clubeRepository;
    @Autowired
    private JogadorRepository jogadorRepository;
    public ClubeResponseDto criarClube(ClubeRequestDto clubeRequestDto) {

        if (!StringUtils.hasText(clubeRequestDto.nome())) {
            throw new BadRequestException(KeyMessages.NOME_CLUBE_OBRIGATORIO);
        }
        if (!StringUtils.hasText(clubeRequestDto.informacao())) {
            throw new BadRequestException(KeyMessages.INFORMACAO_CLUBE_OBRIGATORIO);
        }
        if (clubeRepository.existsByNome(clubeRequestDto.nome())) {
            throw new BadRequestException(KeyMessages.NOME_CLUBE_CADASTRADO);
        }
        ClubeEntities clubeEntities = ClubeMapper.toClubeEntity(clubeRequestDto);
        ClubeEntities salvo = clubeRepository.save(clubeEntities);

        return ClubeMapper.toClubeResponseDTO(salvo);
    }

    public List<ClubeResponseDto> listarClubes() {
        return clubeRepository.findAll()
                .stream()
                .map(ClubeMapper::toClubeResponseDTO)
                .collect( Collectors.toList());
    }

    public ClubeResponseDto buscarClubePorId(Integer id) {
        ClubeEntities clube = clubeRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));
        return ClubeMapper.toClubeResponseDTO(clube);
    }

    public ClubeResponseDto atualizarClube(Integer id, ClubeRequestDto dto) {

        ClubeEntities clube = clubeRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));

        if (clubeRepository.existsByNome(dto.nome())
                && !clube.getNome().equals(dto.nome())) {
            throw new BadRequestException(KeyMessages.NOME_CLUBE_CADASTRADO);
        }
        if (clubeRepository.existsByNome(dto.informacao())
                && !clube.getInformacao().equals(dto.informacao())) {
            throw new BadRequestException(KeyMessages.INFORMACAO_CLUBE_CADASTRADO);
        }

        clube.setNome(dto.nome());
        clube.setInformacao(dto.informacao());

        ClubeEntities salvo = clubeRepository.save(clube);

        return ClubeMapper.toClubeResponseDTO(salvo);
    }

    public void deletarClube(Integer id) {
        ClubeEntities clube = clubeRepository.findByIdWithJogadores(id)
                .orElseThrow(() ->
                        new NotFoundException("Clube com ID " + id + " não encontrado.")
                );
        if (clube.getJogadores() != null && !clube.getJogadores().isEmpty()) {
            throw new BadRequestException(
                    KeyMessages.NAO_PERMITE_EXCLUSAO_CLUBE_COM_JOGADOR);
        }
        clubeRepository.deleteById(id);
    }

    public void deletarClubes() {
            if (jogadorRepository.count() > 0) {
                throw new BadRequestException(
                        KeyMessages.NAO_PERMITE_EXCLUSAO_CLUBE_COM_JOGADOR);
            }
        clubeRepository.deleteAll();
    }

    public ClubeDetalhadoResponseDto buscarClubeDetalhado(Integer id) {
        ClubeEntities clube = clubeRepository.findByIdWithJogadores(id)
                .orElseThrow(() ->
                        new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));
        return ClubeMapper.toDetalhadoDto(clube);
    }

}
