package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.dto.EscalacaoRequestDto;
import com.letssoccer.letssoccer.dto.EscalacaoResponseDto;
import com.letssoccer.letssoccer.dto.JogadorResponseDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.EscalacaoEntity;
import com.letssoccer.letssoccer.entities.JogadorEntity;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import com.letssoccer.letssoccer.messages.exception.NotFoundException;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.EscalacaoRepository;
import com.letssoccer.letssoccer.repositories.JogadorRepository;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EscalacaoService {

    private final JogadorRepository jogadorRepository;
    private final ClubeRepository clubeRepository;
    private final EscalacaoRepository escalacaoRepository;
    private final UsuarioRepository usuarioRepository;

    public EscalacaoService(JogadorRepository jogadorRepository,
                            ClubeRepository clubeRepository,
                            EscalacaoRepository escalacaoRepository,
                            UsuarioRepository usuarioRepository) {
        this.jogadorRepository = jogadorRepository;
        this.clubeRepository = clubeRepository;
        this.escalacaoRepository = escalacaoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public List<JogadorResponseDto> salvarEscalacao(Integer clubeId, String esquema,
                                                    EscalacaoRequestDto dto, String email) {

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Usuário não encontrado"));

        ClubeEntity clube = clubeRepository.findById(clubeId)
                .orElseThrow(() -> new NotFoundException(KeyMessages.CLUBE_NAO_ENCONTRADO));

        List<Integer> jogadoresIds = dto.getJogadoresIds();

        if (jogadoresIds == null || jogadoresIds.size() != 11) {
            throw new BadRequestException("A escalação deve conter exatamente 11 jogadores");
        }

        Set<Integer> idsUnicos = new HashSet<>(jogadoresIds);
        if (idsUnicos.size() != 11) {
            throw new BadRequestException("Não é permitido jogadores duplicados");
        }

        List<JogadorEntity> jogadores = jogadorRepository.findAllById(jogadoresIds);
        if (jogadores.size() != 11) {
            throw new BadRequestException("Um ou mais jogadores não foram encontrados");
        }

        boolean algumDeOutroClube = jogadores.stream()
                .anyMatch(j -> !j.getClube().getId().equals(clubeId));
        if (algumDeOutroClube) {
            throw new BadRequestException("Todos os jogadores devem pertencer ao clube informado");
        }

        Map<Integer, JogadorEntity> porId = jogadores.stream()
                .collect(Collectors.toMap(JogadorEntity::getId, j -> j));

        List<JogadorEntity> ordenados = jogadoresIds.stream()
                .map(porId::get)
                .toList();

        EscalacaoEntity escalacao = new EscalacaoEntity();
        escalacao.setUsuario(usuario);
        escalacao.setClube(clube);
        escalacao.setEsquema(esquema);
        escalacao.setJogadores(ordenados);

        EscalacaoEntity salva = escalacaoRepository.save(escalacao);

        return toJogadoresDto(salva);
    }

    @Transactional(readOnly = true)
    public EscalacaoResponseDto buscarEscalacao(Integer clubeId, String email) {

        EscalacaoEntity escalacao = escalacaoRepository
                .findTopByClubeIdAndUsuarioEmailOrderByDataCriacaoDesc(clubeId, email)
                .orElseThrow(() -> new NotFoundException("Escalação não encontrada"));

        return new EscalacaoResponseDto(
                "Escalação encontrada",
                escalacao.getEsquema(),
                toJogadoresDto(escalacao)
        );
    }

    private List<JogadorResponseDto> toJogadoresDto(EscalacaoEntity escalacao) {
        return escalacao.getJogadores().stream()
                .map(j -> new JogadorResponseDto(j.getId(), j.getNome(), j.getPosicao(), j.getFoto()))
                .toList();
    }
}