package com.f1ruta.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.f1ruta.algoritmo.RutaF1TSP;
import com.f1ruta.algoritmo.DijkstraRutas;
import com.f1ruta.algoritmo.BFSRutas;
import com.f1ruta.algoritmo.RutaF1TSP.Circuito;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class RutaService {

    private final ObjectMapper mapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // ================== Helpers ==================

    private List<Circuito> cargarCircuitos() {
        try (InputStream is = getClass().getResourceAsStream("/data/circuitos.json")) {
            if (is == null) {
                throw new IllegalStateException("No se encontró /data/circuitos.json en resources.");
            }
            return mapper.readValue(is, new TypeReference<List<Circuito>>() {});
        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo /data/circuitos.json", e);
        }
    }

    /** Lista de TODOS los puntos (para marcar en el mapa, si querés). */
    private List<Map<String, Object>> puntos(List<Circuito> circuitos) {
        List<Map<String, Object>> ps = new ArrayList<>(circuitos.size());
        for (Circuito c : circuitos) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("nombre", c.nombre);
            m.put("lat", c.latitud);
            m.put("lon", c.longitud);
            ps.add(m);
        }
        return ps;
    }

    /** Puntos en el orden de una ruta dada por nombres (para polilínea). */
    private List<Map<String, Object>> puntosEnOrden(List<Circuito> circuitos, List<String> nombres) {
        // Index rápido por nombre (case-insensitive)
        Map<String, Circuito> porNombre = new HashMap<>();
        for (Circuito c : circuitos) {
            porNombre.put(c.nombre.toLowerCase(Locale.ROOT), c);
        }
        List<Map<String, Object>> out = new ArrayList<>();
        for (String nombre : nombres) {
            Circuito c = porNombre.get(nombre.toLowerCase(Locale.ROOT));
            if (c == null) continue; // por si hubiera alguna diferencia de escritura
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("nombre", c.nombre);
            m.put("lat", c.latitud);
            m.put("lon", c.longitud);
            out.add(m);
        }
        return out;
    }

    // ================== Algoritmos ==================

    /** Heurística TSP: Nearest Neighbor + 2-opt */
    public Map<String, Object> ejecutarNearest() {
        List<Circuito> circuitos = cargarCircuitos();
        var res = RutaF1TSP.calcularRuta(circuitos);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("algoritmo", "Nearest Neighbor + 2-opt");
        out.put("cantidad_circuitos", circuitos.size());
        out.put("ruta", res.ruta());
        out.put("km_totales", res.kmTotales());

        // Para Leaflet
        out.put("puntos", puntos(circuitos)); // todos los marcadores (opcional)
        out.put("puntos_ruta", puntosEnOrden(circuitos, res.ruta())); // polilínea en orden
        return out;
    }

    /** Dijkstra: ruta mínima entre dos circuitos */
    public Map<String, Object> ejecutarDijkstra(String origen, String destino) {
        List<Circuito> circuitos = cargarCircuitos();
        var res = DijkstraRutas.calcularRuta(circuitos, origen, destino);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("algoritmo", "Dijkstra (ruta mínima entre dos circuitos)");
        out.put("origen", origen);
        out.put("destino", destino);
        out.put("ruta", res.ruta());
        out.put("km_totales", res.kmTotales());

        // Para Leaflet
        out.put("puntos", puntos(circuitos)); // todos (si querés mostrar todo el set)
        out.put("puntos_ruta", puntosEnOrden(circuitos, res.ruta())); // polilínea origen→…→destino
        return out;
    }

    /** BFS con umbral de conexión (maxKm) para definir aristas */
    public Map<String, Object> ejecutarBFS(String inicio, Double maxKm) {
        List<Circuito> circuitos = cargarCircuitos();
        double umbral = (maxKm == null || maxKm <= 0) ? 3000.0 : maxKm; // default 3000 km
        var res = BFSRutas.ejecutar(circuitos, inicio, umbral);

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("algoritmo", "BFS (arista si distancia <= maxKm)");
        out.put("inicio", res.inicio());
        out.put("max_km", res.maxKm());
        out.put("orden_visita", res.ordenVisita());
        out.put("niveles", res.niveles());
        out.put("no_alcanzados", res.noAlcanzados());
        out.put("total_visitados", res.ordenVisita().size());
        out.put("total_circuitos", circuitos.size());

        // Para Leaflet
        out.put("puntos", puntos(circuitos)); // todos los circuitos (marcadores)
        out.put("puntos_orden", puntosEnOrden(circuitos, res.ordenVisita())); // polilínea por niveles
        return out;
    }
}
