package com.letssoccer.letssoccer.repositories;

import com.letssoccer.letssoccer.entities.EsquemaTaticoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface EsquemaTaticoRepository
        extends JpaRepository<EsquemaTaticoEntity, Integer> {

    boolean existsByClubeId(Integer clubeId);
    Optional<EsquemaTaticoEntity> findByClubeId(Integer clubeId);
}