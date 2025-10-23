package com.fitnesstracker.fit.Model;
import java.sql.Time;

public class Serie {
    private int repeticiones;
    private double peso;
    private Time duracion;

    public Serie(int repeticiones, double peso) {
        this.repeticiones = repeticiones;
        this.peso = peso;
    }

    public Serie(Time duracion) {
        this.duracion = duracion;
    }

    public int getRepeticiones() {
        return repeticiones;
    }

    public void setRepeticiones(int repeticiones) {
        this.repeticiones = repeticiones;
    }

    public double getPeso() {
        return peso;
    }

    public void setPeso(double peso) {
        this.peso = peso;
    }

    public Time getDuracion() {
        return duracion;
    }

    public void setDuracion(Time duracion) {
        this.duracion = duracion;
    }
}