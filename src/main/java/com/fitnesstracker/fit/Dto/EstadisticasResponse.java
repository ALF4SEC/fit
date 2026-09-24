package com.fitnesstracker.fit.Dto;

import java.time.LocalDate;
import java.util.Map;

import com.fitnesstracker.fit.Model.GrupoMuscular;

/**
 * Totales de las sesiones de un periodo. desde y hasta son null si no se
 * ha limitado el periodo por ese lado.
 */
public record EstadisticasResponse(
        LocalDate desde,
        LocalDate hasta,
        int sesiones,
        int ejercicios,
        int series,
        int repeticionesTotales,
        double volumenTotal,
        int minutosTotales,
        Map<GrupoMuscular, Integer> seriesPorGrupoMuscular) {
}
