package com.f1ruta.controller;

import com.f1ruta.service.RutaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// Swagger / Springdoc
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@RequestMapping("/api/algoritmos")
public class AlgoritmoController {

    private final RutaService servicio;

    public AlgoritmoController(RutaService servicio) {
        this.servicio = servicio;
    }

    @Operation(summary = "Greedy TSP: Nearest Neighbor + 2-opt")
    @GetMapping("/nearest")
    public ResponseEntity<Map<String, Object>> ejecutarNearest() {
        return ResponseEntity.ok(servicio.ejecutarNearest());
    }

    @Operation(summary = "Dijkstra: ruta mínima entre dos circuitos (Haversine)")
    @GetMapping("/dijkstra")
    public ResponseEntity<Map<String, Object>> ejecutarDijkstra(
            @Parameter(example = "Abu Dhabi (EAU)") @RequestParam(name = "origen") String origen,
            @Parameter(example = "Sao Paulo (Brasil)") @RequestParam(name = "destino") String destino
    ) {
        return ResponseEntity.ok(servicio.ejecutarDijkstra(origen.trim(), destino.trim()));
    }

    @Operation(
        summary = "BFS: recorrido por niveles usando umbral de conexión",
        description = "Se conecta un circuito con otro si la distancia Haversine es <= max_km. Devuelve orden de visita, niveles y nodos no alcanzados."
    )
    @GetMapping("/bfs")
    public ResponseEntity<Map<String, Object>> ejecutarBFS(
            @Parameter(description = "Circuito de inicio, exactamente como en el JSON",
                       example = "Monaco (Mónaco)")
            @RequestParam(name = "inicio") String inicio,
            @Parameter(description = "Umbral de conexión en km (default 3000)", example = "3000")
            @RequestParam(name = "max_km", required = false) Double maxKm
    ) {
        return ResponseEntity.ok(servicio.ejecutarBFS(inicio, maxKm));
    }

    @Operation(summary = "Branch & Bound TSP: Ruta óptima exacta desde un origen")
    @GetMapping("/branch-bound")
    public ResponseEntity<Map<String, Object>> ejecutarBranchBound(
            @Parameter(description = "Circuito de origen para iniciar la ruta óptima", example = "Monaco (Mónaco)")
            @RequestParam(name = "origen", required = false) String origen
    ) {
        return ResponseEntity.ok(servicio.ejecutarBranchBound(origen));
    }

    @Operation(
        summary = "Mergesort: Ordena circuitos por criterio (Divide y Vencerás)",
        description = "Criterios disponibles: latitud, longitud, nombre. Orden: asc o desc."
    )
    @GetMapping("/mergesort")
    public ResponseEntity<Map<String, Object>> ejecutarMergesort(
            @Parameter(description = "Criterio de ordenamiento: latitud, longitud, nombre", example = "latitud")
            @RequestParam(name = "criterio", required = false, defaultValue = "nombre") String criterio,
            @Parameter(description = "Orden: asc (ascendente) o desc (descendente)", example = "asc")
            @RequestParam(name = "orden", required = false, defaultValue = "asc") String orden
    ) {
        return ResponseEntity.ok(servicio.ejecutarMergesort(criterio, orden));
    }
}
