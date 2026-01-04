package com.letssoccer.letssoccer.repositories;

import com.letssoccer.letssoccer.entities.JogadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JogadorRepository extends JpaRepository<JogadorEntity, Integer> {
    List<JogadorEntity> findByClubeId(Integer clubeId);
    boolean existsByNomeIgnoreCaseAndClubeId(String nome, Integer clubeId);
}