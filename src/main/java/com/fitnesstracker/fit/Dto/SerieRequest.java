package com.fitnesstracker.fit.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Datos de una serie. Tiene que llevar repeticiones o duración (o las dos);
 * el peso es opcional.
 */
public record SerieRequest(
        @Positive(message = "Las repeticiones tienen que ser mayores que 0")
        Integer repeticiones,
        @PositiveOrZero(message = "El peso no puede ser negativo")
        Double peso,
        @Positive(message = "La duración tiene que ser mayor que 0")
        Integer duracionSegundos) {

    @JsonIgnore
    @AssertTrue(message = "La serie necesita repeticiones o duración")
    public boolean isRepeticionesODuracion() {
        return repeticiones != null || duracionSegundos != null;
    }
}
