# fit

API REST en Spring Boot para registrar sesiones de entrenamiento: qué ejercicios se hacen en cada sesión, con sus series de repeticiones, peso o tiempo, y cómo evoluciona cada ejercicio. Es la versión como servicio web de la idea de [fitnessTracker24-25](https://github.com/ALF4SEC/fitnessTracker24-25).

## Modelo

```
Sesion 1 ── * Ejercicio 1 ── * Serie
```

| Entidad | Campos |
|---|---|
| `Sesion` | Fecha (obligatoria), hora de inicio y hora de fin. La duración en minutos se calcula a partir de las horas. |
| `Ejercicio` | Nombre (obligatorio), descripción y grupo muscular: `ESPALDA`, `BRAZOS`, `PIERNAS`, `ABDOMEN` o `PECHO`. |
| `Serie` | Repeticiones, peso en kg y duración en segundos. Tiene que llevar repeticiones o duración, así que sirve tanto para series con peso como para ejercicios por tiempo, como una plancha. |

Borrar una sesión borra sus ejercicios, y borrar un ejercicio borra sus series. El volumen de una serie es repeticiones × peso, y el de un ejercicio es la suma del de sus series.

## API

### Sesiones

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/sesiones?desde=&hasta=` | Lista las sesiones de la más reciente a la más antigua. Las dos fechas (`aaaa-mm-dd`) son opcionales. |
| `GET` | `/api/sesiones/{id}` | Sesión con sus ejercicios y series. |
| `POST` | `/api/sesiones` | Crea una sesión. Puede incluir ya los ejercicios y sus series. |
| `PUT` | `/api/sesiones/{id}` | Modifica la fecha y las horas. Si el cuerpo trae `ejercicios`, sustituyen a los que había; si no, se mantienen. |
| `DELETE` | `/api/sesiones/{id}` | Borra la sesión. |
| `POST` | `/api/sesiones/{id}/ejercicios` | Añade un ejercicio a la sesión. |

### Ejercicios

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/ejercicios/{id}` | Ejercicio con sus series. |
| `PUT` | `/api/ejercicios/{id}` | Modifica el ejercicio. Si el cuerpo trae `series`, sustituyen a las que había; si no, se mantienen. |
| `DELETE` | `/api/ejercicios/{id}` | Borra el ejercicio. |
| `POST` | `/api/ejercicios/{id}/series` | Añade una serie al ejercicio. |
| `GET` | `/api/ejercicios/progreso?nombre=` | Evolución de un ejercicio en todas las sesiones, de la más antigua a la más reciente: series, repeticiones totales, peso máximo y volumen. El nombre no distingue mayúsculas. |

### Series

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/api/series/{id}` | Consulta una serie. |
| `PUT` | `/api/series/{id}` | Modifica una serie. |
| `DELETE` | `/api/series/{id}` | Borra una serie. |

### Estadísticas

`GET /api/estadisticas?desde=&hasta=` devuelve, para las sesiones del periodo, el número de sesiones, ejercicios y series, las repeticiones totales, el volumen total, los minutos entrenados y cuántas series se han hecho de cada grupo muscular. Las dos fechas son opcionales.

### Ejemplo

```bash
curl -X POST http://localhost:8080/api/sesiones \
  -H "Content-Type: application/json" \
  -d '{
    "fecha": "2026-09-20",
    "inicioSesion": "18:00",
    "finSesion": "19:15",
    "ejercicios": [
      {
        "nombre": "Press banca",
        "grupoMuscular": "PECHO",
        "series": [
          { "repeticiones": 10, "peso": 60 },
          { "repeticiones": 8, "peso": 70 }
        ]
      },
      {
        "nombre": "Plancha",
        "grupoMuscular": "ABDOMEN",
        "series": [ { "duracionSegundos": 60 } ]
      }
    ]
  }'
```

Responde con `201 Created`, una cabecera `Location` con la ruta de la sesión nueva y la sesión completa con sus ids, la duración (75 minutos) y el volumen de cada ejercicio.

### Errores

Los errores se devuelven en formato [ProblemDetail](https://www.rfc-editor.org/rfc/rfc9457) con el código HTTP, un título y una explicación en español:

```json
{
  "title": "Datos no válidos",
  "status": 400,
  "detail": "La hora de fin no puede ser anterior a la de inicio",
  "errores": ["La hora de fin no puede ser anterior a la de inicio"]
}
```

- `400`: datos que no pasan la validación, JSON mal formado, un grupo muscular que no existe, un parámetro con formato incorrecto o que falta, o un periodo en el que `desde` es posterior a `hasta`.
- `404`: la sesión, el ejercicio o la serie no existen.

## Estructura

```
src/main/java/com/fitnesstracker/fit/
  Model/        entidades JPA: Sesion, Ejercicio, Serie y GrupoMuscular
  Repository/   repositorios de Spring Data JPA
  Dto/          datos de entrada (con sus validaciones) y de salida de la API
  Service/      lógica: sesiones, ejercicios, series y estadísticas
  Controller/   controladores REST
  Exception/    excepciones propias y ManejadorErrores, que las convierte en respuestas HTTP
src/test/java/com/fitnesstracker/fit/
  ApiTests.java pruebas de integración de toda la API
```

Las entidades no salen directamente por la API: los controladores reciben y devuelven DTO (`record` de Java), así que el formato del JSON no depende de cómo se guardan los datos.

## Tecnologías

- Java 25
- Spring Boot 3.5.6: Spring Web, Spring Data JPA, Bean Validation y DevTools
- MySQL en uso normal y H2 en memoria para pruebas
- JUnit 5 y MockMvc para los tests
- Maven, con el wrapper incluido

## Ejecución

### Con MySQL

Por defecto la aplicación se conecta a `jdbc:mysql://localhost:3306/fit` con el usuario `root` sin contraseña, y crea la base de datos si no existe. Se puede cambiar con variables de entorno:

| Variable | Valor por defecto |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/fit?createDatabaseIfNotExist=true&serverTimezone=Europe/Madrid` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | (vacía) |

```bash
DB_PASSWORD=mi_clave ./mvnw spring-boot:run
```

Hibernate crea y actualiza las tablas a partir de las entidades (`ddl-auto=update`).

### Sin MySQL (perfil `local`)

El perfil `local` usa una base de datos H2 en memoria, así que no hace falta instalar nada. Los datos se pierden al parar la aplicación.

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```

Con este perfil, la consola de H2 está en `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:fit`, usuario `sa`, sin contraseña).

En Windows se usa `mvnw.cmd` en lugar de `./mvnw`.

### Tests

```bash
./mvnw test
```

Los tests usan el perfil `local` y prueban la API completa: creación y consulta de sesiones con ejercicios y series, validaciones, errores 404, gestión de ejercicios y series por separado, sustitución de ejercicios al actualizar, filtros por fecha, progreso de un ejercicio y estadísticas.
