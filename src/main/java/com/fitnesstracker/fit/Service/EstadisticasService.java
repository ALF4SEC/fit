package com.fitnesstracker.fit.Service;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitnesstracker.fit.Dto.EstadisticasResponse;
import com.fitnesstracker.fit.Model.Ejercicio;
import com.fitnesstracker.fit.Model.GrupoMuscular;
import com.fitnesstracker.fit.Model.Serie;
import com.fitnesstracker.fit.Model.Sesion;

@Service
public class EstadisticasService {

    private final SesionService sesionService;

    public EstadisticasService(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    @Transactional(readOnly = true)
    public EstadisticasResponse calcular(LocalDate desde, LocalDate hasta) {
        List<Sesion> sesiones = sesionService.buscarEntre(desde, hasta);
        List<Ejercicio> ejercicios = sesiones.stream().flatMap(s -> s.getEjercicios().stream()).toList();
        List<Serie> series = ejercicios.stream().flatMap(e -> e.getSeries().stream()).toList();

        int repeticiones = series.stream()
                .map(Serie::getRepeticiones).filter(Objects::nonNull)
                .mapToInt(Integer::intValue).sum();
        double volumen = series.stream().mapToDouble(Serie::getVolumen).sum();
        int minutos = sesiones.stream()
                .map(Sesion::getDuracionMinutos).filter(Objects::nonNull)
                .mapToInt(Integer::intValue).sum();

        // Todos los grupos aparecen en la respuesta, aunque tengan 0 series
        Map<GrupoMuscular, Integer> porGrupo = new EnumMap<>(GrupoMuscular.class);
        for (GrupoMuscular grupo : GrupoMuscular.values()) {
            porGrupo.put(grupo, 0);
        }
        for (Ejercicio ejercicio : ejercicios) {
            if (ejercicio.getGrupoMuscular() != null) {
                porGrupo.merge(ejercicio.getGrupoMuscular(), ejercicio.getSeries().size(), Integer::sum);
            }
        }

        return new EstadisticasResponse(desde, hasta, sesiones.size(), ejercicios.size(), series.size(),
                repeticiones, volumen, minutos, porGrupo);
    }
}
