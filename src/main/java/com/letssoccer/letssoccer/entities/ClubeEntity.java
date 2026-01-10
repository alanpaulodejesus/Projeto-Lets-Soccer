package com.letssoccer.letssoccer.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clube")
public class ClubeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotNull
    private String nome;
    @NotNull
    private String informacao;

    @OneToMany(
            mappedBy = "clube",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<JogadorEntity> jogadores = new ArrayList<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getInformacao() {
        return informacao;
    }
    public void setInformacao(String informacao) {
        this.informacao = informacao;
    }

    public List<JogadorEntity> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<JogadorEntity> jogadores) {
        this.jogadores = jogadores;
    }
}
