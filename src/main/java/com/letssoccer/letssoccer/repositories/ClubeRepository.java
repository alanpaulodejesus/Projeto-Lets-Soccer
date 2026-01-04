package com.letssoccer.letssoccer.repositories;

import com.letssoccer.letssoccer.entities.ClubeEntities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ClubeRepository extends JpaRepository <ClubeEntities, Integer>{
    List<ClubeEntities> findAllByNome(String nome);
    boolean existsByNome(String nome);
}
