package com.fitnesstracker.fit.Dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

/**
 * Datos de una sesión. Al crear, puede incluir ya los ejercicios con sus
 * series. Al actualizar, si ejercicios es null se mantienen los que había y
 * si no, se sustituyen por los nuevos.
 */
public record SesionRequest(
        @NotNull(message = "La fecha es obligatoria")
        LocalDate fecha,
        LocalTime inicioSesion,
        LocalTime finSesion,
        List<@Valid EjercicioRequest> ejercicios) {

    @JsonIgnore
    @AssertTrue(message = "La hora de fin no puede ser anterior a la de inicio")
    public boolean isHorarioValido() {
        return inicioSesion == null || finSesion == null || !finSesion.isBefore(inicioSesion);
    }
}
