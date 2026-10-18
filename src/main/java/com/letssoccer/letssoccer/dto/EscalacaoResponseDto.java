package com.letssoccer.letssoccer.dto;

import java.util.List;

public record EscalacaoResponseDto(
        String mensagem,
        String esquema,
        List<JogadorResponseDto> jogadores
) {}