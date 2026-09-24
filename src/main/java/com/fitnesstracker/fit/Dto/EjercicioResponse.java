package com.fitnesstracker.fit.Dto;

import java.util.List;

import com.fitnesstracker.fit.Model.Ejercicio;
import com.fitnesstracker.fit.Model.GrupoMuscular;

public record EjercicioResponse(
        Long id,
        Long sesionId,
        String nombre,
        String descripcion,
        GrupoMuscular grupoMuscular,
        double volumen,
        List<SerieResponse> series) {

    public static EjercicioResponse de(Ejercicio ejercicio) {
        return new EjercicioResponse(ejercicio.getId(), ejercicio.getSesion().getId(),
                ejercicio.getNombre(), ejercicio.getDescripcion(), ejercicio.getGrupoMuscular(),
                ejercicio.getVolumen(),
                ejercicio.getSeries().stream().map(SerieResponse::de).toList());
    }
}
