package com.fitnesstracker.fit.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitnesstracker.fit.Dto.EjercicioRequest;
import com.fitnesstracker.fit.Dto.EjercicioResponse;
import com.fitnesstracker.fit.Dto.ProgresoResponse;
import com.fitnesstracker.fit.Dto.SerieRequest;
import com.fitnesstracker.fit.Dto.SerieResponse;
import com.fitnesstracker.fit.Exception.DatosInvalidosException;
import com.fitnesstracker.fit.Exception.RecursoNoEncontradoException;
import com.fitnesstracker.fit.Model.Ejercicio;
import com.fitnesstracker.fit.Model.Serie;
import com.fitnesstracker.fit.Repository.EjercicioRepository;

@Service
@Transactional
public class EjercicioService {

    private final EjercicioRepository ejercicioRepository;

    public EjercicioService(EjercicioRepository ejercicioRepository) {
        this.ejercicioRepository = ejercicioRepository;
    }

    @Transactional(readOnly = true)
    public EjercicioResponse obtener(Long id) {
        return EjercicioResponse.de(buscar(id));
    }

    public EjercicioResponse actualizar(Long id, EjercicioRequest datos) {
        Ejercicio ejercicio = buscar(id);
        ejercicio.setNombre(datos.nombre().trim());
        ejercicio.setDescripcion(datos.descripcion());
        ejercicio.setGrupoMuscular(datos.grupoMuscular());

        // Si llegan series, sustituyen a las que había
        if (datos.series() != null) {
            new ArrayList<>(ejercicio.getSeries()).forEach(ejercicio::removeSerie);
            datos.series().forEach(s -> ejercicio.addSerie(nuevaSerie(s)));
        }
        ejercicioRepository.flush();
        return EjercicioResponse.de(ejercicio);
    }

    public void eliminar(Long id) {
        Ejercicio ejercicio = buscar(id);
        ejercicio.getSesion().removeEjercicio(ejercicio);
        ejercicioRepository.delete(ejercicio);
    }

    public SerieResponse anadirSerie(Long ejercicioId, SerieRequest datos) {
        Ejercicio ejercicio = buscar(ejercicioId);
        Serie serie = nuevaSerie(datos);
        ejercicio.addSerie(serie);
        ejercicioRepository.flush();
        return SerieResponse.de(serie);
    }

    /** Evolución de un ejercicio (por nombre, sin distinguir mayúsculas) en todas las sesiones. */
    @Transactional(readOnly = true)
    public List<ProgresoResponse> progreso(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new DatosInvalidosException("Hay que indicar el nombre del ejercicio");
        }
        return ejercicioRepository.findByNombreIgnoreCaseOrderBySesion_FechaAscSesion_InicioSesionAsc(nombre.trim())
                .stream()
                .map(EjercicioService::resumir)
                .toList();
    }

    private static ProgresoResponse resumir(Ejercicio ejercicio) {
        List<Serie> series = ejercicio.getSeries();
        int repeticiones = series.stream()
                .map(Serie::getRepeticiones).filter(Objects::nonNull)
                .mapToInt(Integer::intValue).sum();
        Double pesoMaximo = series.stream()
                .map(Serie::getPeso).filter(Objects::nonNull)
                .max(Double::compare).orElse(null);
        return new ProgresoResponse(ejercicio.getSesion().getId(), ejercicio.getSesion().getFecha(),
                series.size(), repeticiones, pesoMaximo, ejercicio.getVolumen());
    }

    static Ejercicio nuevoEjercicio(EjercicioRequest datos) {
        Ejercicio ejercicio = new Ejercicio(datos.nombre().trim(), datos.descripcion(), datos.grupoMuscular());
        if (datos.series() != null) {
            datos.series().forEach(s -> ejercicio.addSerie(nuevaSerie(s)));
        }
        return ejercicio;
    }

    static Serie nuevaSerie(SerieRequest datos) {
        return new Serie(datos.repeticiones(), datos.peso(), datos.duracionSegundos());
    }

    private Ejercicio buscar(Long id) {
        return ejercicioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("ningún ejercicio", id));
    }
}
