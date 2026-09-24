package com.fitnesstracker.fit.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitnesstracker.fit.Model.Sesion;

public interface SesionRepository extends JpaRepository<Sesion, Long> {

    List<Sesion> findAllByOrderByFechaDescInicioSesionDesc();

    List<Sesion> findByFechaGreaterThanEqualOrderByFechaDescInicioSesionDesc(LocalDate desde);

    List<Sesion> findByFechaLessThanEqualOrderByFechaDescInicioSesionDesc(LocalDate hasta);

    List<Sesion> findByFechaBetweenOrderByFechaDescInicioSesionDesc(LocalDate desde, LocalDate hasta);
}
