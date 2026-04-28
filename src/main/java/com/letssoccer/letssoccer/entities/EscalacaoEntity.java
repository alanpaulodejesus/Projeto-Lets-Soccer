package com.letssoccer.letssoccer.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "escalacao")
public class EscalacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String esquema;

    @ManyToOne
    @JoinColumn(name = "clube_id", nullable = false)
    private ClubeEntity clube;

    @ManyToMany
    @JoinTable(
            name = "escalacao_jogadores",
            joinColumns = @JoinColumn(name = "escalacao_id"),
            inverseJoinColumns = @JoinColumn(name = "jogador_id")
    )
    private List<JogadorEntity> jogadores;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @PrePersist
    public void prePersist() {
        this.dataCriacao = LocalDateTime.now();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEsquema() {
        return esquema;
    }

    public void setEsquema(String esquema) {
        this.esquema = esquema;
    }

    public ClubeEntity getClube() {
        return clube;
    }

    public void setClube(ClubeEntity clube) {
        this.clube = clube;
    }

    public List<JogadorEntity> getJogadores() {
        return jogadores;
    }

    public void setJogadores(List<JogadorEntity> jogadores) {
        this.jogadores = jogadores;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
}