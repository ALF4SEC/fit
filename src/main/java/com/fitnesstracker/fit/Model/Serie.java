package com.fitnesstracker.fit.Model;
import java.io.Serializable;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Una serie de un ejercicio. Puede ser de repeticiones (con o sin peso) o por
 * tiempo, como una plancha, en cuyo caso se guarda la duración en segundos.
 */
@Entity
public class Serie implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer repeticiones;
    private Double peso;
    private Integer duracionSegundos;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ejercicio_id")
    private Ejercicio ejercicio;

    // Constructor vacío que necesita JPA
    protected Serie() {
    }

    public Serie(Integer repeticiones, Double peso, Integer duracionSegundos) {
        this.repeticiones = repeticiones;
        this.peso = peso;
        this.duracionSegundos = duracionSegundos;
    }

    // Repeticiones x peso, o 0 si la serie no tiene alguno de los dos
    public double getVolumen() {
        if (repeticiones == null || peso == null) {
            return 0;
        }
        return repeticiones * peso;
    }

    public Long getId() {
        return id;
    }

    public Integer getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(Integer repeticiones) {
        this.repeticiones = repeticiones;
    }

    public Double getPeso() {
        return peso;
    }

    public void setPeso(Double peso) {
        this.peso = peso;
    }

    public Integer getDuracionSegundos() {
        return duracionSegundos;
    }

    public void setDuracionSegundos(Integer duracionSegundos) {
        this.duracionSegundos = duracionSegundos;
    }

    public Ejercicio getEjercicio() {
        return ejercicio;
    }

    public void setEjercicio(Ejercicio ejercicio) {
        this.ejercicio = ejercicio;
    }
}
