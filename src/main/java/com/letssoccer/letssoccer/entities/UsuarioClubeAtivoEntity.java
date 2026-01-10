package com.letssoccer.letssoccer.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_clube_ativo",
        uniqueConstraints = @UniqueConstraint(columnNames = "usuario_id"))
public class UsuarioClubeAtivoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @ManyToOne
    @JoinColumn(name = "clube_id", nullable = false)
    private ClubeEntity clube;

    private LocalDateTime dataSelecao;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public ClubeEntity getClube() {
        return clube;
    }

    public void setClube(ClubeEntity clube) {
        this.clube = clube;
    }

    public LocalDateTime getDataSelecao() {
        return dataSelecao;
    }

    public void setDataSelecao(LocalDateTime dataSelecao) {
        this.dataSelecao = dataSelecao;
    }

}
