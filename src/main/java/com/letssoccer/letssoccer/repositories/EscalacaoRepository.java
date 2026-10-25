package com.letssoccer.letssoccer.repositories;

import com.letssoccer.letssoccer.entities.EscalacaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EscalacaoRepository extends JpaRepository<EscalacaoEntity, Integer> {

    Optional<EscalacaoEntity> findTopByClubeIdAndUsuarioEmailOrderByDataCriacaoDesc(Integer clubeId, String email);
    boolean existsByClubeIdAndUsuarioEmail(Integer clubeId, String email);
}