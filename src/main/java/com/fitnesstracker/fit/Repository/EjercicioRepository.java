package com.fitnesstracker.fit.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitnesstracker.fit.Model.Ejercicio;


public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

    // Todas las veces que se ha hecho un ejercicio, de la sesión más antigua a la más reciente
    List<Ejercicio> findByNombreIgnoreCaseOrderBySesion_FechaAscSesion_InicioSesionAsc(String nombre);
}
