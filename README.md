# fit

Comienzo de un backend en Spring Boot para una aplicación de seguimiento de entrenamientos. Es una nueva versión, como servicio web, de la idea de [fitnessTracker24-25](https://github.com/ALF4SEC/fitnessTracker24-25). De momento solo tiene el modelo de datos y los repositorios, y todavía no hay controladores ni API.

## Modelo

| Entidad | Campos | Relaciones |
|---|---|---|
| `Sesion` | Fecha, hora de inicio y hora de fin | Tiene varios `Ejercicio` |
| `Ejercicio` | Nombre y descripción | Tiene varias `Serie` |
| `Serie` | Repeticiones y peso, o una duración para ejercicios por tiempo | |

Las relaciones son `@OneToMany` con borrado en cascada. Los repositorios extienden `JpaRepository`.

## Tecnologías

- Java 25
- Spring Boot 3.5.6: Spring Web, Spring Data JPA y DevTools
- MySQL (`mysql-connector-j`)
- Lombok
- Maven, con el wrapper incluido

## Estado y pendientes

- Falta configurar la conexión a MySQL en `application.properties`, que ahora solo tiene el nombre de la aplicación. Hasta entonces la aplicación no arranca.
- `SerieRepository` está declarado sobre la entidad `Sesion` en lugar de `Serie`.
- Las entidades no tienen el constructor vacío que exige JPA.
- `Ejercicio` y `Serie` no tienen getter del `id`.

## Ejecución

Cuando la base de datos esté configurada:

```bash
./mvnw spring-boot:run
```

En Windows se usa `mvnw.cmd spring-boot:run`.
