package com.fitnesstracker.fit.Model;
import java.sql.Date;
import java.sql.Time;

import jakarta.persistence.Entity;


public class Sesion {
    private Date fecha;
    private Time inicioSesion;
    private Time finSesion;
    private Ejercicio[] ejercicios;

    public Sesion(Date fecha, Time inicioSesion, Time finSesion, Ejercicio[] ejercicios) {
        this.fecha = fecha;
        this.inicioSesion = inicioSesion;
        this.finSesion = finSesion;
        this.ejercicios = ejercicios;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }


    public Time getInicioSesion() {
        return inicioSesion;
    }

    public void setInicioSesion(Time inicioSesion) {
        this.inicioSesion = inicioSesion;
    }

    public Time getFinSesion() {
        return finSesion;
    }

    public void setFinSesion(Time finSesion) {
        this.finSesion = finSesion;
    }

    public Ejercicio[] getEjercicios() {
        return ejercicios;
    }

    public void setEjercicios(Ejercicio[] ejercicios) {
        this.ejercicios = ejercicios;
    }
}
