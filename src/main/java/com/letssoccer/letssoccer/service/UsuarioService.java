package com.letssoccer.letssoccer.service;

import com.letssoccer.letssoccer.dto.UsuarioCadastroDto;
import com.letssoccer.letssoccer.entities.UsuarioEntity;
import com.letssoccer.letssoccer.messages.exception.BadRequestException;
import com.letssoccer.letssoccer.repositories.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UsuarioService {

    private final UsuarioRepository repository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository repository,
                          PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public void cadastrar(UsuarioCadastroDto dto) {

        if (repository.existsByEmail(dto.email())) {
            throw new BadRequestException("E-mail já cadastrado");
        }

        if (!dto.senha().equals(dto.confirmacaoSenha())) {
            throw new BadRequestException("Senhas não conferem");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setPerfil(Perfil.USER);

        repository.save(usuario);
    }
}
