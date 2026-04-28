package com.letssoccer.letssoccer.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class EscalacaoRequestDto {

    @NotEmpty
    private List<Integer> jogadoresIds;

    public List<Integer> getJogadoresIds() {
        return jogadoresIds;
    }

    public void setJogadoresIds(List<Integer> jogadoresIds) {
        this.jogadoresIds = jogadoresIds;
    }
}
