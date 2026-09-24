package com.fitnesstracker.fit.Model;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

@Entity
public class Ejercicio implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String nombre;
    private String descripcion;
    @Enumerated(EnumType.STRING)
    private GrupoMuscular grupoMuscular;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sesion_id")
    private Sesion sesion;

    @OneToMany(mappedBy = "ejercicio", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<Serie> series = new ArrayList<>();

    // Constructor vacío que necesita JPA
    protected Ejercicio() {
    }

    public Ejercicio(String nombre, String descripcion, GrupoMuscular grupoMuscular) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.grupoMuscular = grupoMuscular;
    }

    // Mantiene sincronizados los dos lados de la relación
    public void addSerie(Serie serie) {
        series.add(serie);
        serie.setEjercicio(this);
    }

    public void removeSerie(Serie serie) {
        series.remove(serie);
        serie.setEjercicio(null);
    }

    // Suma de repeticiones x peso de todas las series
    public double getVolumen() {
        return series.stream().mapToDouble(Serie::getVolumen).sum();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public GrupoMuscular getGrupoMuscular() {
        return grupoMuscular;
    }

    public void setGrupoMuscular(GrupoMuscular grupoMuscular) {
        this.grupoMuscular = grupoMuscular;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public List<Serie> getSeries() {
        return series;
    }
}
