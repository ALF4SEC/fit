package com.fitnesstracker.fit.Controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitnesstracker.fit.Dto.EjercicioRequest;
import com.fitnesstracker.fit.Dto.EjercicioResponse;
import com.fitnesstracker.fit.Dto.SesionRequest;
import com.fitnesstracker.fit.Dto.SesionResponse;
import com.fitnesstracker.fit.Service.SesionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sesiones")
public class SesionController {

    private final SesionService sesionService;

    public SesionController(SesionService sesionService) {
        this.sesionService = sesionService;
    }

    // GET /api/sesiones?desde=2026-09-01&hasta=2026-09-30 (los dos parámetros son opcionales)
    @GetMapping
    public List<SesionResponse> listar(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return sesionService.listar(desde, hasta);
    }

    @GetMapping("/{id}")
    public SesionResponse obtener(@PathVariable Long id) {
        return sesionService.obtener(id);
    }

    @PostMapping
    public ResponseEntity<SesionResponse> crear(@Valid @RequestBody SesionRequest datos) {
        SesionResponse sesion = sesionService.crear(datos);
        return ResponseEntity.created(URI.create("/api/sesiones/" + sesion.id())).body(sesion);
    }

    @PutMapping("/{id}")
    public SesionResponse actualizar(@PathVariable Long id, @Valid @RequestBody SesionRequest datos) {
        return sesionService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sesionService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/ejercicios")
    public ResponseEntity<EjercicioResponse> anadirEjercicio(@PathVariable Long id,
            @Valid @RequestBody EjercicioRequest datos) {
        EjercicioResponse ejercicio = sesionService.anadirEjercicio(id, datos);
        return ResponseEntity.created(URI.create("/api/ejercicios/" + ejercicio.id())).body(ejercicio);
    }
}
