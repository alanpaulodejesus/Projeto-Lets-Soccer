package com.letssoccer.letssoccer.repositories;

import com.letssoccer.letssoccer.entities.UsuarioEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository
        extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
