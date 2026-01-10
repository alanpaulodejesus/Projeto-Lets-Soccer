package com.letssoccer.letssoccer.repositories;

import com.letssoccer.letssoccer.entities.UsuarioClubeAtivoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioClubeAtivoRepository
        extends JpaRepository<UsuarioClubeAtivoEntity, Long> {

    Optional<UsuarioClubeAtivoEntity> findByUsuarioId(Long usuarioId);
}

