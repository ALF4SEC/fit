package com.fitnesstracker.fit.Controller;

import java.net.URI;
import java.util.List;

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
import com.fitnesstracker.fit.Dto.ProgresoResponse;
import com.fitnesstracker.fit.Dto.SerieRequest;
import com.fitnesstracker.fit.Dto.SerieResponse;
import com.fitnesstracker.fit.Service.EjercicioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/ejercicios")
public class EjercicioController {

    private final EjercicioService ejercicioService;

    public EjercicioController(EjercicioService ejercicioService) {
        this.ejercicioService = ejercicioService;
    }

    // GET /api/ejercicios/progreso?nombre=Press banca
    @GetMapping("/progreso")
    public List<ProgresoResponse> progreso(@RequestParam String nombre) {
        return ejercicioService.progreso(nombre);
    }

    @GetMapping("/{id}")
    public EjercicioResponse obtener(@PathVariable Long id) {
        return ejercicioService.obtener(id);
    }

    @PutMapping("/{id}")
    public EjercicioResponse actualizar(@PathVariable Long id, @Valid @RequestBody EjercicioRequest datos) {
        return ejercicioService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        ejercicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/series")
    public ResponseEntity<SerieResponse> anadirSerie(@PathVariable Long id, @Valid @RequestBody SerieRequest datos) {
        SerieResponse serie = ejercicioService.anadirSerie(id, datos);
        return ResponseEntity.created(URI.create("/api/series/" + serie.id())).body(serie);
    }
}
