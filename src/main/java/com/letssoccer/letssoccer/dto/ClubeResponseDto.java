package com.letssoccer.letssoccer.dto;

import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import jakarta.validation.constraints.NotNull;

public record ClubeResponseDto(Integer id, @NotNull(message = KeyMessages.NOME_CLUBE_OBRIGATORIO)
                              String nome,
                               @NotNull(message = KeyMessages.INFORMACAO_CLUBE_OBRIGATORIO)
                              String informacao) {
}
