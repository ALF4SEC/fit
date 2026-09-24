package com.fitnesstracker.fit.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.fitnesstracker.fit.Model.Serie;

public interface SerieRepository extends JpaRepository<Serie, Long> {

}
