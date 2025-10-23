package com.fitnesstracker.fit.Model;

public class Ejercicio {
    private String nombre;
    private String descripcion;
    private Serie[] series;

    public Ejercicio(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public Ejercicio(String nombre, String descripcion, Serie[] series) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.series = series;
    }

    public Serie[] getSeries() {
        return series;
    }

    public void setSeries(Serie[] series) {
        this.series = series;
    }

    public Ejercicio(String nombre) {
        this.nombre = nombre;
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
}
