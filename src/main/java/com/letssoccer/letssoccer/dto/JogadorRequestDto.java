package com.letssoccer.letssoccer.dto;

import jakarta.validation.constraints.NotBlank;

public record JogadorRequestDto(
        @NotBlank String nome,
        @NotBlank String posicao
) {}

