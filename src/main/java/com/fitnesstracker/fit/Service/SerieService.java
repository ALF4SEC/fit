package com.fitnesstracker.fit.Service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fitnesstracker.fit.Dto.SerieRequest;
import com.fitnesstracker.fit.Dto.SerieResponse;
import com.fitnesstracker.fit.Exception.RecursoNoEncontradoException;
import com.fitnesstracker.fit.Model.Serie;
import com.fitnesstracker.fit.Repository.SerieRepository;

@Service
@Transactional
public class SerieService {

    private final SerieRepository serieRepository;

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    @Transactional(readOnly = true)
    public SerieResponse obtener(Long id) {
        return SerieResponse.de(buscar(id));
    }

    public SerieResponse actualizar(Long id, SerieRequest datos) {
        Serie serie = buscar(id);
        serie.setRepeticiones(datos.repeticiones());
        serie.setPeso(datos.peso());
        serie.setDuracionSegundos(datos.duracionSegundos());
        return SerieResponse.de(serie);
    }

    public void eliminar(Long id) {
        Serie serie = buscar(id);
        serie.getEjercicio().removeSerie(serie);
        serieRepository.delete(serie);
    }

    private Serie buscar(Long id) {
        return serieRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("ninguna serie", id));
    }
}
