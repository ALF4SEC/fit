package com.fitnesstracker.fit;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.jayway.jsonpath.JsonPath;

/**
 * Pruebas de la API completa contra una base de datos H2 en memoria.
 * Cada test se ejecuta en una transacción que se deshace al terminar.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("local")
@Transactional
class ApiTests {

    private static final String SESION = """
            {
              "fecha": "2026-09-20",
              "inicioSesion": "18:00",
              "finSesion": "19:15",
              "ejercicios": [
                {
                  "nombre": "Press banca",
                  "descripcion": "Con barra",
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
            }
            """;

    @Autowired
    private MockMvc mvc;

    private MvcResult crearSesion(String json) throws Exception {
        return mvc.perform(post("/api/sesiones").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isCreated())
                .andReturn();
    }

    private long id(MvcResult resultado, String ruta) throws Exception {
        return ((Number) JsonPath.read(resultado.getResponse().getContentAsString(), ruta)).longValue();
    }

    @Test
    void creaYConsultaUnaSesionCompleta() throws Exception {
        MvcResult creada = crearSesion(SESION);
        long id = id(creada, "$.id");

        mvc.perform(get("/api/sesiones/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fecha").value("2026-09-20"))
                .andExpect(jsonPath("$.duracionMinutos").value(75))
                .andExpect(jsonPath("$.ejercicios", hasSize(2)))
                .andExpect(jsonPath("$.ejercicios[0].nombre").value("Press banca"))
                .andExpect(jsonPath("$.ejercicios[0].volumen").value(1160.0))
                .andExpect(jsonPath("$.ejercicios[0].series", hasSize(2)))
                .andExpect(jsonPath("$.ejercicios[1].series[0].duracionSegundos").value(60))
                .andExpect(jsonPath("$.ejercicios[1].series[0].volumen").value(0.0));
    }

    @Test
    void laCabeceraLocationApuntaALaSesionCreada() throws Exception {
        MvcResult creada = crearSesion(SESION);
        mvc.perform(get(creada.getResponse().getHeader("Location"))).andExpect(status().isOk());
    }

    @Test
    void rechazaDatosNoValidos() throws Exception {
        // Sin fecha
        mvc.perform(post("/api/sesiones").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0]").value("La fecha es obligatoria"));

        // La hora de fin antes que la de inicio
        mvc.perform(post("/api/sesiones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fecha\":\"2026-09-20\",\"inicioSesion\":\"19:00\",\"finSesion\":\"18:00\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0]").value("La hora de fin no puede ser anterior a la de inicio"));

        // Una serie sin repeticiones ni duración, dentro de un ejercicio
        mvc.perform(post("/api/sesiones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fecha\":\"2026-09-20\",\"ejercicios\":[{\"nombre\":\"Remo\",\"series\":[{\"peso\":40}]}]}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0]").value("La serie necesita repeticiones o duración"));

        // Un grupo muscular que no existe
        mvc.perform(post("/api/sesiones").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fecha\":\"2026-09-20\",\"ejercicios\":[{\"nombre\":\"Remo\",\"grupoMuscular\":\"COLA\"}]}"))
                .andExpect(status().isBadRequest());

        // Un id que no es un número
        mvc.perform(get("/api/sesiones/abc")).andExpect(status().isBadRequest());

        // Falta un parámetro obligatorio
        mvc.perform(get("/api/ejercicios/progreso"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Falta el parámetro obligatorio nombre"));
    }

    @Test
    void devuelve404SiNoExiste() throws Exception {
        mvc.perform(get("/api/sesiones/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("No existe ninguna sesión con id 9999"));
        mvc.perform(get("/api/ejercicios/9999")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/series/9999")).andExpect(status().isNotFound());
    }

    @Test
    void gestionaEjerciciosYSeriesPorSeparado() throws Exception {
        long sesionId = id(crearSesion("{\"fecha\":\"2026-09-21\"}"), "$.id");

        // Añadir un ejercicio a la sesión
        MvcResult ejercicio = mvc.perform(post("/api/sesiones/" + sesionId + "/ejercicios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Sentadilla\",\"grupoMuscular\":\"PIERNAS\"}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.sesionId").value(sesionId))
                .andReturn();
        long ejercicioId = id(ejercicio, "$.id");

        // Añadir una serie al ejercicio
        MvcResult serie = mvc.perform(post("/api/ejercicios/" + ejercicioId + "/series")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repeticiones\":5,\"peso\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.volumen").value(500.0))
                .andReturn();
        long serieId = id(serie, "$.id");

        // Modificar la serie
        mvc.perform(put("/api/series/" + serieId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"repeticiones\":6,\"peso\":100}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.repeticiones").value(6));
        mvc.perform(get("/api/ejercicios/" + ejercicioId))
                .andExpect(jsonPath("$.volumen").value(600.0));

        // Renombrar el ejercicio sin tocar sus series (series = null)
        mvc.perform(put("/api/ejercicios/" + ejercicioId).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"Sentadilla trasera\",\"grupoMuscular\":\"PIERNAS\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Sentadilla trasera"))
                .andExpect(jsonPath("$.series", hasSize(1)));

        // Borrar la serie y después el ejercicio
        mvc.perform(delete("/api/series/" + serieId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/ejercicios/" + ejercicioId)).andExpect(jsonPath("$.series", hasSize(0)));
        mvc.perform(delete("/api/ejercicios/" + ejercicioId)).andExpect(status().isNoContent());
        mvc.perform(get("/api/sesiones/" + sesionId)).andExpect(jsonPath("$.ejercicios", hasSize(0)));
    }

    @Test
    void actualizarSesionConEjerciciosLosSustituye() throws Exception {
        long id = id(crearSesion(SESION), "$.id");

        // Sin ejercicios en el cuerpo: se cambia la fecha y se mantienen los ejercicios
        mvc.perform(put("/api/sesiones/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fecha\":\"2026-09-22\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fecha").value("2026-09-22"))
                .andExpect(jsonPath("$.duracionMinutos").value(nullValue()))
                .andExpect(jsonPath("$.ejercicios", hasSize(2)));

        // Con ejercicios: sustituyen a los anteriores
        mvc.perform(put("/api/sesiones/" + id).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fecha\":\"2026-09-22\",\"ejercicios\":[{\"nombre\":\"Dominadas\",\"series\":[{\"repeticiones\":12}]}]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ejercicios", hasSize(1)))
                .andExpect(jsonPath("$.ejercicios[0].nombre").value("Dominadas"));

        mvc.perform(delete("/api/sesiones/" + id)).andExpect(status().isNoContent());
        mvc.perform(get("/api/sesiones/" + id)).andExpect(status().isNotFound());
    }

    @Test
    void filtraSesionesPorFecha() throws Exception {
        crearSesion("{\"fecha\":\"2026-09-01\"}");
        crearSesion("{\"fecha\":\"2026-09-15\"}");
        crearSesion("{\"fecha\":\"2026-10-01\"}");

        mvc.perform(get("/api/sesiones"))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].fecha").value("2026-10-01"));
        mvc.perform(get("/api/sesiones").param("desde", "2026-09-10").param("hasta", "2026-09-30"))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].fecha").value("2026-09-15"));
        mvc.perform(get("/api/sesiones").param("desde", "2026-09-10"))
                .andExpect(jsonPath("$", hasSize(2)));
        mvc.perform(get("/api/sesiones").param("hasta", "2026-09-10"))
                .andExpect(jsonPath("$", hasSize(1)));
        mvc.perform(get("/api/sesiones").param("desde", "2026-10-01").param("hasta", "2026-09-01"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void calculaElProgresoDeUnEjercicio() throws Exception {
        crearSesion(SESION);
        crearSesion("""
                {"fecha":"2026-09-27","ejercicios":[
                  {"nombre":"press BANCA","series":[{"repeticiones":6,"peso":75},{"repeticiones":6,"peso":75}]}
                ]}
                """);

        mvc.perform(get("/api/ejercicios/progreso").param("nombre", "Press banca"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].fecha").value("2026-09-20"))
                .andExpect(jsonPath("$[0].pesoMaximo").value(70.0))
                .andExpect(jsonPath("$[0].repeticionesTotales").value(18))
                .andExpect(jsonPath("$[1].fecha").value("2026-09-27"))
                .andExpect(jsonPath("$[1].pesoMaximo").value(75.0))
                .andExpect(jsonPath("$[1].volumen").value(900.0));
    }

    @Test
    void calculaLasEstadisticas() throws Exception {
        crearSesion(SESION);
        crearSesion("{\"fecha\":\"2026-08-01\",\"ejercicios\":[{\"nombre\":\"Curl\",\"grupoMuscular\":\"BRAZOS\",\"series\":[{\"repeticiones\":12,\"peso\":10}]}]}");

        mvc.perform(get("/api/estadisticas").param("desde", "2026-09-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sesiones").value(1))
                .andExpect(jsonPath("$.ejercicios").value(2))
                .andExpect(jsonPath("$.series").value(3))
                .andExpect(jsonPath("$.repeticionesTotales").value(18))
                .andExpect(jsonPath("$.volumenTotal").value(1160.0))
                .andExpect(jsonPath("$.minutosTotales").value(75))
                .andExpect(jsonPath("$.seriesPorGrupoMuscular.PECHO").value(2))
                .andExpect(jsonPath("$.seriesPorGrupoMuscular.ABDOMEN").value(1))
                .andExpect(jsonPath("$.seriesPorGrupoMuscular.BRAZOS").value(0));

        mvc.perform(get("/api/estadisticas"))
                .andExpect(jsonPath("$.sesiones").value(2))
                .andExpect(jsonPath("$.seriesPorGrupoMuscular.BRAZOS").value(1));
    }
}
