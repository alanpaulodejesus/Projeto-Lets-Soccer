package com.letssoccer.letssoccer.dto;

import jakarta.validation.constraints.NotNull;

public record UsuarioClubeRequestDto(
        @NotNull Integer clubeId
) {}

