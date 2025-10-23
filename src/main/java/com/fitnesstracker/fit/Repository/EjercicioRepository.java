package com.fitnesstracker.fit.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitnesstracker.fit.Model.Ejercicio;


public interface EjercicioRepository extends JpaRepository<Ejercicio, Long> {

}
