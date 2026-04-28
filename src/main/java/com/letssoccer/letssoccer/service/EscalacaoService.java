package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.dto.EscalacaoRequestDto;
import com.letssoccer.letssoccer.entities.ClubeEntity;
import com.letssoccer.letssoccer.entities.EscalacaoEntity;
import com.letssoccer.letssoccer.entities.JogadorEntity;
import com.letssoccer.letssoccer.repositories.ClubeRepository;
import com.letssoccer.letssoccer.repositories.EscalacaoRepository;
import com.letssoccer.letssoccer.repositories.JogadorRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EscalacaoService {

    private final JogadorRepository jogadorRepository;
    private final ClubeRepository clubeRepository;
    private final EscalacaoRepository escalacaoRepository;

    public EscalacaoService(JogadorRepository jogadorRepository,
                            ClubeRepository clubeRepository,
                            EscalacaoRepository escalacaoRepository) {
        this.jogadorRepository = jogadorRepository;
        this.clubeRepository = clubeRepository;
        this.escalacaoRepository = escalacaoRepository;
    }

    public void salvarEscalacao(Integer clubeId, String esquema, EscalacaoRequestDto dto) {

        // 1. Validar clube
        ClubeEntity clube = clubeRepository.findById(clubeId)
                .orElseThrow(() -> new RuntimeException("Clube não encontrado"));

        List<Integer> jogadoresIds = dto.getJogadoresIds();

        // 2. Validar quantidade (EXATAMENTE 11)
        if (jogadoresIds == null || jogadoresIds.size() != 11) {
            throw new RuntimeException("A escalação deve conter exatamente 11 jogadores");
        }

        // 3. Validar duplicidade
        Set<Integer> idsUnicos = new HashSet<>(jogadoresIds);
        if (idsUnicos.size() != 11) {
            throw new RuntimeException("Não é permitido jogadores duplicados");
        }

        // 4. Buscar jogadores
        List<JogadorEntity> jogadores = jogadorRepository.findAllById(jogadoresIds);

        if (jogadores.size() != 11) {
            throw new RuntimeException("Um ou mais jogadores não foram encontrados");
        }

        // 5. Validar se TODOS pertencem ao clube
        boolean algumDeOutroClube = jogadores.stream()
                .anyMatch(j -> !j.getClube().getId().equals(clubeId));

        if (algumDeOutroClube) {
            throw new RuntimeException("Todos os jogadores devem pertencer ao clube informado");
        }

        // 6. Criar e salvar escalação
        EscalacaoEntity escalacao = new EscalacaoEntity();
        escalacao.setClube(clube);
        escalacao.setEsquema(esquema);
        escalacao.setJogadores(jogadores);

        escalacaoRepository.save(escalacao);
    }
}