package com.fitnesstracker.fit.Exception;

/** Se lanza cuando los datos de una petición no tienen sentido. Se responde con 400. */
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}
