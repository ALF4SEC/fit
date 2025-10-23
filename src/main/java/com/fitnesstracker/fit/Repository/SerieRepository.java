package com.fitnesstracker.fit.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitnesstracker.fit.Model.Sesion;

public interface SerieRepository extends JpaRepository<Sesion, Long> {

}
