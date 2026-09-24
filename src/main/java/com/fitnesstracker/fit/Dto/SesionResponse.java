package com.fitnesstracker.fit.Dto;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.fitnesstracker.fit.Model.Sesion;

public record SesionResponse(
        Long id,
        LocalDate fecha,
        LocalTime inicioSesion,
        LocalTime finSesion,
        Integer duracionMinutos,
        List<EjercicioResponse> ejercicios) {

    public static SesionResponse de(Sesion sesion) {
        return new SesionResponse(sesion.getId(), sesion.getFecha(), sesion.getInicioSesion(),
                sesion.getFinSesion(), sesion.getDuracionMinutos(),
                sesion.getEjercicios().stream().map(EjercicioResponse::de).toList());
    }
}
