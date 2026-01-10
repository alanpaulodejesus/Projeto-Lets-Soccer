package com.letssoccer.letssoccer.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "esquema_tatico")
public class EsquemaTaticoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private boolean esquema442;
    private boolean esquema352;
    private boolean esquema541;
    private boolean esquema244;

    @OneToOne
    @JoinColumn(name = "clube_id", nullable = false)
    private ClubeEntity clube;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isEsquema442() {
        return esquema442;
    }

    public void setEsquema442(boolean esquema442) {
        this.esquema442 = esquema442;
    }

    public boolean isEsquema352() {
        return esquema352;
    }

    public void setEsquema352(boolean esquema352) {
        this.esquema352 = esquema352;
    }

    public boolean isEsquema541() {
        return esquema541;
    }

    public void setEsquema541(boolean esquema541) {
        this.esquema541 = esquema541;
    }

    public boolean isEsquema244() {
        return esquema244;
    }

    public void setEsquema244(boolean esquema244) {
        this.esquema244 = esquema244;
    }

    public ClubeEntity getClube() {
        return clube;
    }

    public void setClube(ClubeEntity clube) {
        this.clube = clube;
    }

}
