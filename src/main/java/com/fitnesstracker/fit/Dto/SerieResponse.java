package com.fitnesstracker.fit.Dto;

import com.fitnesstracker.fit.Model.Serie;

public record SerieResponse(
        Long id,
        Integer repeticiones,
        Double peso,
        Integer duracionSegundos,
        double volumen) {

    public static SerieResponse de(Serie serie) {
        return new SerieResponse(serie.getId(), serie.getRepeticiones(), serie.getPeso(),
                serie.getDuracionSegundos(), serie.getVolumen());
    }
}
