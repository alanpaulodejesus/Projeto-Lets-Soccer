package com.letssoccer.letssoccer.repositories;

import com.letssoccer.letssoccer.entities.ClubeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClubeRepository extends JpaRepository <ClubeEntity, Integer>{
    List<ClubeEntity> findAllByNome(String nome);
    boolean existsByNome(String nome);

    @Query("""
        SELECT c FROM ClubeEntity c
        LEFT JOIN FETCH c.jogadores
        WHERE c.id = :id
    """)
    Optional<ClubeEntity> findByIdWithJogadores(@Param("id") Integer id);
}
