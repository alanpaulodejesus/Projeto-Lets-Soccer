package com.letssoccer.letssoccer.dto;

public record UsuarioCadastroDto(
        String nome,
        String email,
        String senha,
        String confirmacaoSenha
) {}
