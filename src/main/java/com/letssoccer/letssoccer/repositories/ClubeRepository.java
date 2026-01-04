package com.letssoccer.letssoccer.repositories;

import com.letssoccer.letssoccer.entities.ClubeEntities;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClubeRepository extends JpaRepository <ClubeEntities, Integer>{
    List<ClubeEntities> findAllByNome(String nome);
    boolean existsByNome(String nome);

    @Query("""
        SELECT c FROM ClubeEntities c
        LEFT JOIN FETCH c.jogadores
        WHERE c.id = :id
    """)
    Optional<ClubeEntities> findByIdWithJogadores(@Param("id") Integer id);
}
