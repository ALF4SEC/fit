package com.fitnesstracker.fit.Model;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate fecha;
    private LocalTime inicioSesion;
    private LocalTime finSesion;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Ejercicio> ejercicios;

    public Sesion(LocalDate fecha, LocalTime inicioSesion, LocalTime finSesion, List<Ejercicio> ejercicios) {
        this.fecha = fecha;
        this.inicioSesion = inicioSesion;
        this.finSesion = finSesion;
        this.ejercicios = ejercicios;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }


    public LocalTime getInicioSesion() {
        return inicioSesion;
    }

    public void setInicioSesion(LocalTime inicioSesion) {
        this.inicioSesion = inicioSesion;
    }

    public LocalTime getFinSesion() {
        return finSesion;
    }

    public void setFinSesion(LocalTime finSesion) {
        this.finSesion = finSesion;
    }

    public List<Ejercicio> getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(List<Ejercicio> ejercicios) {
        this.ejercicios = ejercicios;
    }
}
