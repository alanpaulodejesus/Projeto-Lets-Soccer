package com.letssoccer.letssoccer.dto;

import java.util.List;

public record ClubeDetalhadoResponseDto(
        Integer id,
        String nome,
        String informacao,
        List<JogadorResponseDto> jogadores
) {}
