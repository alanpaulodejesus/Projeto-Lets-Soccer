package com.letssoccer.letssoccer.dto;

import java.util.List;

public class EscalacaoResponseDto {

    private String mensagem;
    private String esquema;
    private List<Integer> jogadores;

    public EscalacaoResponseDto(String mensagem, String esquema, List<Integer> jogadores) {
        this.mensagem = mensagem;
        this.esquema = esquema;
        this.jogadores = jogadores;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getEsquema() {
        return esquema;
    }

    public List<Integer> getJogadores() {
        return jogadores;
    }
}