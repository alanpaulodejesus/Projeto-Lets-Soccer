package com.letssoccer.letssoccer.dto;

import com.letssoccer.letssoccer.messages.exception.KeyMessages;
import jakarta.validation.constraints.NotBlank;

public record ClubeRequestDto(@NotBlank (message = KeyMessages.NOME_CLUBE_OBRIGATORIO)
                              String nome,
                              @NotBlank (message = KeyMessages.INFORMACAO_CLUBE_OBRIGATORIO)
                              String informacao) {
}
