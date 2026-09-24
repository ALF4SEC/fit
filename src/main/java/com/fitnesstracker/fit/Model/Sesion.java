package com.fitnesstracker.fit.Model;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;

@Entity
public class Sesion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private LocalDate fecha;
    private LocalTime inicioSesion;
    private LocalTime finSesion;

    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<Ejercicio> ejercicios = new ArrayList<>();

    // Constructor vacío que necesita JPA
    protected Sesion() {
    }

    public Sesion(LocalDate fecha, LocalTime inicioSesion, LocalTime finSesion) {
        this.fecha = fecha;
        this.inicioSesion = inicioSesion;
        this.finSesion = finSesion;
    }

    // Mantiene sincronizados los dos lados de la relación
    public void addEjercicio(Ejercicio ejercicio) {
        ejercicios.add(ejercicio);
        ejercicio.setSesion(this);
    }

    public void removeEjercicio(Ejercicio ejercicio) {
        ejercicios.remove(ejercicio);
        ejercicio.setSesion(null);
    }

    // Duración en minutos, o null si falta la hora de inicio o la de fin
    public Integer getDuracionMinutos() {
        if (inicioSesion == null || finSesion == null) {
            return null;
        }
        return (int) Duration.between(inicioSesion, finSesion).toMinutes();
    }

    public Long getId() {
        return id;
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
}
