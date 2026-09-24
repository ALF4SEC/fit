package com.fitnesstracker.fit.Controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fitnesstracker.fit.Dto.SerieRequest;
import com.fitnesstracker.fit.Dto.SerieResponse;
import com.fitnesstracker.fit.Service.SerieService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/series")
public class SerieController {

    private final SerieService serieService;

    public SerieController(SerieService serieService) {
        this.serieService = serieService;
    }

    @GetMapping("/{id}")
    public SerieResponse obtener(@PathVariable Long id) {
        return serieService.obtener(id);
    }

    @PutMapping("/{id}")
    public SerieResponse actualizar(@PathVariable Long id, @Valid @RequestBody SerieRequest datos) {
        return serieService.actualizar(id, datos);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        serieService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
