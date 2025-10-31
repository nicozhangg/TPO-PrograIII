package com.f1ruta.algoritmo;

import java.util.*;

/** BFS (anchura) sobre grafo implícito: hay arista si distancia <= maxKm */
public class BFSRutas {

    public static record Resultado(
            String inicio,
            double maxKm,
            List<String> ordenVisita,
            Map<Integer, List<String>> niveles,
            List<String> noAlcanzados
    ) {}

    /** Usamos el mismo DTO que el TSP (nombre, latitud, longitud) */
    public static class Circuito extends RutaF1TSP.Circuito { }

    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2);
        double dphi = p2 - p1;
        double dlambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dphi/2)*Math.sin(dphi/2)
                + Math.cos(p1)*Math.cos(p2)*Math.sin(dlambda/2)*Math.sin(dlambda/2);
        return 2 * R * Math.asin(Math.sqrt(a));
    }

    /** Matriz de distancias */
    private static double[][] distancias(List<RutaF1TSP.Circuito> cs) {
        int n = cs.size();
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) {
            var a = cs.get(i);
            for (int j = 0; j < n; j++) {
                if (i == j) { d[i][j] = 0; continue; }
                var b = cs.get(j);
                d[i][j] = haversine(a.latitud, a.longitud, b.latitud, b.longitud);
            }
        }
        return d;
    }

    /**
     * BFS desde 'inicioNombre'. Hay arista u->v si distancia(u,v) <= maxKm.
     * Devuelve orden de visita y nodos por nivel.
     */
    public static Resultado ejecutar(List<RutaF1TSP.Circuito> circuitos,
                                     String inicioNombre,
                                     double maxKm) {
        if (inicioNombre == null || inicioNombre.isBlank())
            throw new IllegalArgumentException("El circuito de inicio no puede ser vacío");
        inicioNombre = inicioNombre.trim().toLowerCase(Locale.ROOT);

        int n = circuitos.size();
        Map<String,Integer> idx = new HashMap<>();
        for (int i = 0; i < n; i++) {
            idx.put(circuitos.get(i).nombre.toLowerCase(Locale.ROOT), i);
        }
        Integer s = idx.get(inicioNombre);
        if (s == null) throw new IllegalArgumentException("Circuito inicio no encontrado: " + inicioNombre);

        double[][] d = distancias(circuitos);

        // BFS
        boolean[] vis = new boolean[n];
        int[] level = new int[n];
        Arrays.fill(level, -1);

        Queue<Integer> q = new ArrayDeque<>();
        vis[s] = true; level[s] = 0; q.add(s);

        List<String> orden = new ArrayList<>();
        Map<Integer, List<String>> niveles = new LinkedHashMap<>();
        niveles.put(0, new ArrayList<>(List.of(circuitos.get(s).nombre)));

        while (!q.isEmpty()) {
            int u = q.poll();
            orden.add(circuitos.get(u).nombre);

            for (int v = 0; v < n; v++) {
                if (!vis[v] && d[u][v] <= maxKm) {
                    vis[v] = true;
                    level[v] = level[u] + 1;
                    q.add(v);
                    niveles.computeIfAbsent(level[v], k -> new ArrayList<>())
                           .add(circuitos.get(v).nombre);
                }
            }
        }

        List<String> noAlcanzados = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (!vis[i]) noAlcanzados.add(circuitos.get(i).nombre);
        }

        return new Resultado(circuitos.get(s).nombre, maxKm, orden, niveles, noAlcanzados);
    }
}
