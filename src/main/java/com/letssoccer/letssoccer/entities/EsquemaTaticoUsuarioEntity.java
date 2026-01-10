package com.letssoccer.letssoccer.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "esquema_tatico_usuario")
public class EsquemaTaticoUsuarioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long usuarioId;

    @ManyToOne
    @JoinColumn(name = "clube_id", nullable = false)
    private ClubeEntities clube;

    @Column(nullable = false)
    private String esquema;

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

    public ClubeEntities getClube() {
        return clube;
    }

    public void setClube(ClubeEntities clube) {
        this.clube = clube;
    }

    public String getEsquema() {
        return esquema;
    }

    public void setEsquema(String esquema) {
        this.esquema = esquema;
    }

}
