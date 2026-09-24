package com.fitnesstracker.fit.Dto;

import java.util.List;

import com.fitnesstracker.fit.Model.GrupoMuscular;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos de un ejercicio. Al crear, series puede venir vacío o no venir.
 * Al actualizar, si series es null se mantienen las que había y si no, se
 * sustituyen por las nuevas.
 */
public record EjercicioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
        String nombre,
        @Size(max = 255, message = "La descripción no puede tener más de 255 caracteres")
        String descripcion,
        GrupoMuscular grupoMuscular,
        List<@Valid SerieRequest> series) {
}
