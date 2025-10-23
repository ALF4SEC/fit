package com.fitnesstracker.fit.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitnesstracker.fit.Model.Sesion;

public interface SesionRepository extends JpaRepository<Sesion, Long> {

}