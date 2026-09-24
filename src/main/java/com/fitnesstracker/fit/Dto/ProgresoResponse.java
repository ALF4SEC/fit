package com.fitnesstracker.fit.Dto;

import java.time.LocalDate;

/**
 * Resumen de un ejercicio en una sesión concreta, para ver su evolución.
 * pesoMaximo es null si ninguna serie tenía peso.
 */
public record ProgresoResponse(
        Long sesionId,
        LocalDate fecha,
        int series,
        int repeticionesTotales,
        Double pesoMaximo,
        double volumen) {
}
