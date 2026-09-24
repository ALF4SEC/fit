package com.fitnesstracker.fit.Exception;

/** Se lanza cuando se pide una sesión, ejercicio o serie que no existe. Se responde con 404. */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String recurso, Long id) {
        super("No existe " + recurso + " con id " + id);
    }
}
