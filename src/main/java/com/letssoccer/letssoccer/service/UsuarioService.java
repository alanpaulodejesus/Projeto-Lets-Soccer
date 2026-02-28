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

        if (dto.email() == null || dto.email().isBlank()) {
            throw new BadRequestException("Email é obrigatório");
        }
        if (dto.senha() == null || dto.senha().isBlank()) {
            throw new BadRequestException("Senha é obrigatória");
        }
        if (dto.confirmacaoSenha() == null || dto.confirmacaoSenha().isBlank()) {
            throw new BadRequestException("Senha é obrigatória");
        }
        if (!dto.senha().equals(dto.confirmacaoSenha())) {
            throw new BadRequestException("Senhas não conferem");
        }
        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new BadRequestException("Nome é obrigatório");
        }
        if (repository.existsByEmail(dto.email())) {
            throw new BadRequestException("E-mail já cadastrado");
        }

        UsuarioEntity usuario = new UsuarioEntity();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));
        usuario.setPerfil(Perfil.USER);

        repository.save(usuario);
    }
}
