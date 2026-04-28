package com.letssoccer.letssoccer.repositories;

import com.letssoccer.letssoccer.entities.EscalacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EscalacaoRepository
        extends JpaRepository<EscalacaoEntity, Integer> {

    List<EscalacaoEntity> findByClubeId(Integer clubeId);

    Optional<EscalacaoEntity> findTopByClubeIdOrderByDataCriacaoDesc(Integer clubeId);

    boolean existsByClubeId(Integer clubeId);
}