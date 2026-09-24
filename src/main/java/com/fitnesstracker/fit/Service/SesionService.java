package com.fitnesstracker.fit.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitnesstracker.fit.Dto.EjercicioRequest;
import com.fitnesstracker.fit.Dto.EjercicioResponse;
import com.fitnesstracker.fit.Dto.SesionRequest;
import com.fitnesstracker.fit.Dto.SesionResponse;
import com.fitnesstracker.fit.Exception.DatosInvalidosException;
import com.fitnesstracker.fit.Exception.RecursoNoEncontradoException;
import com.fitnesstracker.fit.Model.Ejercicio;
import com.fitnesstracker.fit.Model.Sesion;
import com.fitnesstracker.fit.Repository.SesionRepository;

@Service
@Transactional
public class SesionService {

    private final SesionRepository sesionRepository;

    public SesionService(SesionRepository sesionRepository) {
        this.sesionRepository = sesionRepository;
    }

    @Transactional(readOnly = true)
    public List<SesionResponse> listar(LocalDate desde, LocalDate hasta) {
        return buscarEntre(desde, hasta).stream().map(SesionResponse::de).toList();
    }

    @Transactional(readOnly = true)
    public SesionResponse obtener(Long id) {
        return SesionResponse.de(buscar(id));
    }

    public SesionResponse crear(SesionRequest datos) {
        Sesion sesion = new Sesion(datos.fecha(), datos.inicioSesion(), datos.finSesion());
        if (datos.ejercicios() != null) {
            datos.ejercicios().forEach(e -> sesion.addEjercicio(EjercicioService.nuevoEjercicio(e)));
        }
        return SesionResponse.de(sesionRepository.save(sesion));
    }

    public SesionResponse actualizar(Long id, SesionRequest datos) {
        Sesion sesion = buscar(id);
        sesion.setFecha(datos.fecha());
        sesion.setInicioSesion(datos.inicioSesion());
        sesion.setFinSesion(datos.finSesion());

        // Si llegan ejercicios, sustituyen a los que había
        if (datos.ejercicios() != null) {
            new ArrayList<>(sesion.getEjercicios()).forEach(sesion::removeEjercicio);
            datos.ejercicios().forEach(e -> sesion.addEjercicio(EjercicioService.nuevoEjercicio(e)));
        }
        sesionRepository.flush();
        return SesionResponse.de(sesion);
    }

    public void eliminar(Long id) {
        sesionRepository.delete(buscar(id));
    }

    public EjercicioResponse anadirEjercicio(Long sesionId, EjercicioRequest datos) {
        Sesion sesion = buscar(sesionId);
        Ejercicio ejercicio = EjercicioService.nuevoEjercicio(datos);
        sesion.addEjercicio(ejercicio);
        sesionRepository.flush();
        return EjercicioResponse.de(ejercicio);
    }

    /** Sesiones de un periodo, de la más reciente a la más antigua. desde y hasta pueden ser null. */
    @Transactional(readOnly = true)
    public List<Sesion> buscarEntre(LocalDate desde, LocalDate hasta) {
        if (desde != null && hasta != null) {
            if (desde.isAfter(hasta)) {
                throw new DatosInvalidosException("La fecha 'desde' no puede ser posterior a 'hasta'");
            }
            return sesionRepository.findByFechaBetweenOrderByFechaDescInicioSesionDesc(desde, hasta);
        }
        if (desde != null) {
            return sesionRepository.findByFechaGreaterThanEqualOrderByFechaDescInicioSesionDesc(desde);
        }
        if (hasta != null) {
            return sesionRepository.findByFechaLessThanEqualOrderByFechaDescInicioSesionDesc(hasta);
        }
        return sesionRepository.findAllByOrderByFechaDescInicioSesionDesc();
    }

    private Sesion buscar(Long id) {
        return sesionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("ninguna sesión", id));
    }
}
