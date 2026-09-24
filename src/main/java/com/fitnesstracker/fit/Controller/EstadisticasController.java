package com.fitnesstracker.fit.Controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fitnesstracker.fit.Dto.EstadisticasResponse;
import com.fitnesstracker.fit.Service.EstadisticasService;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    private final EstadisticasService estadisticasService;

    public EstadisticasController(EstadisticasService estadisticasService) {
        this.estadisticasService = estadisticasService;
    }

    // GET /api/estadisticas?desde=2026-09-01&hasta=2026-09-30 (los dos parámetros son opcionales)
    @GetMapping
    public EstadisticasResponse calcular(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return estadisticasService.calcular(desde, hasta);
    }
}
