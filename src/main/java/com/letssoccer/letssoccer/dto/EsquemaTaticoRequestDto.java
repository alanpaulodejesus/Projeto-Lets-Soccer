package com.letssoccer.letssoccer.dto;

import jakarta.validation.constraints.AssertTrue;

public record EsquemaTaticoRequestDto(

        boolean esquema442,
        boolean esquema352,
        boolean esquema541,
        boolean esquema244

) {
    @AssertTrue(message = "Ao menos um esquema tático deve ser selecionado")
    public boolean isAlgumSelecionado() {
        return esquema442 || esquema352 || esquema541 || esquema244;
    }
}