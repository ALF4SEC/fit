package com.fitnesstracker.fit.Exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Convierte las excepciones en respuestas de error con formato ProblemDetail
 * (RFC 9457): un JSON con status, title y detail.
 */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ProblemDetail noEncontrado(RecursoNoEncontradoException e) {
        return problema(HttpStatus.NOT_FOUND, "Recurso no encontrado", e.getMessage());
    }

    @ExceptionHandler(DatosInvalidosException.class)
    public ProblemDetail datosInvalidos(DatosInvalidosException e) {
        return problema(HttpStatus.BAD_REQUEST, "Datos no válidos", e.getMessage());
    }

    // Errores de las anotaciones de validación (@NotNull, @Positive...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail validacion(MethodArgumentNotValidException e) {
        List<String> errores = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .toList();
        ProblemDetail problema = problema(HttpStatus.BAD_REQUEST, "Datos no válidos", String.join(". ", errores));
        problema.setProperty("errores", errores);
        return problema;
    }

    // JSON mal formado o con valores que no encajan (una fecha incorrecta, un grupo muscular que no existe...)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ProblemDetail jsonIncorrecto(HttpMessageNotReadableException e) {
        return problema(HttpStatus.BAD_REQUEST, "Datos no válidos",
                "El cuerpo de la petición no es un JSON válido o tiene valores con un formato incorrecto");
    }

    // Parámetros de la URL con un tipo incorrecto, como /api/sesiones/abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ProblemDetail parametroIncorrecto(MethodArgumentTypeMismatchException e) {
        return problema(HttpStatus.BAD_REQUEST, "Datos no válidos",
                "El valor '" + e.getValue() + "' no es válido para el parámetro " + e.getName());
    }

    // Falta un parámetro obligatorio, como nombre en /api/ejercicios/progreso
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ProblemDetail faltaParametro(MissingServletRequestParameterException e) {
        return problema(HttpStatus.BAD_REQUEST, "Datos no válidos",
                "Falta el parámetro obligatorio " + e.getParameterName());
    }

    private ProblemDetail problema(HttpStatus estado, String titulo, String detalle) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(estado, detalle);
        problema.setTitle(titulo);
        return problema;
    }
}
