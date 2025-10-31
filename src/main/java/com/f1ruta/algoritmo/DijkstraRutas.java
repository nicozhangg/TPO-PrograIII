package com.f1ruta.algoritmo;

import java.util.*;

public class DijkstraRutas {

    public static record Resultado(List<String> ruta, double kmTotales) {}

    /** DTO ya usado en tu TSP (mismo formato que el JSON) */
    public static class Circuito extends RutaF1TSP.Circuito { }

    /** Distancia Haversine en km */
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2);
        double dphi = p2 - p1;
        double dlambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dphi/2)*Math.sin(dphi/2)
                + Math.cos(p1)*Math.cos(p2)*Math.sin(dlambda/2)*Math.sin(dlambda/2);
        return 2 * R * Math.asin(Math.sqrt(a));
    }

    /** Matriz de distancias de grafo completo usando Haversine */
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

    /** Calcula la ruta mínima entre 'origen' y 'destino' (nombres del JSON) */
    public static Resultado calcularRuta(List<RutaF1TSP.Circuito> circuitos,
                                         String origen, String destino) {
        if (origen == null || destino == null)
            throw new IllegalArgumentException("origen/destino no pueden ser nulos");
        origen  = origen.trim().toLowerCase(Locale.ROOT);
        destino = destino.trim().toLowerCase(Locale.ROOT);

        int n = circuitos.size();
        Map<String,Integer> idx = new HashMap<>();
        for (int i = 0; i < n; i++) {
            idx.put(circuitos.get(i).nombre.toLowerCase(Locale.ROOT), i);
        }

        Integer s = idx.get(origen);
        Integer t = idx.get(destino);
        if (s == null) throw new IllegalArgumentException("Circuito origen no encontrado: " + origen);
        if (t == null) throw new IllegalArgumentException("Circuito destino no encontrado: " + destino);
        if (s.equals(t)) return new Resultado(
                List.of(circuitos.get(s).nombre), 0.0
        );

        double[][] d = distancias(circuitos);

        // Dijkstra
        double[] dist = new double[n];
        int[] prev = new int[n];
        boolean[] vis = new boolean[n];
        Arrays.fill(dist, Double.POSITIVE_INFINITY);
        Arrays.fill(prev, -1);
        dist[s] = 0.0;

        // PQ por distancia actual; guardamos índices de nodo
        PriorityQueue<Integer> pq = new PriorityQueue<>(Comparator.comparingDouble(u -> dist[u]));
        pq.offer(s);

        while (!pq.isEmpty()) {
            int u = pq.poll();
            if (vis[u]) continue;
            vis[u] = true;
            if (u == t) break;

            for (int v = 0; v < n; v++) {
                if (u == v) continue;
                double alt = dist[u] + d[u][v];
                if (alt < dist[v]) {
                    dist[v] = alt;
                    prev[v] = u;
                    pq.offer(v);
                }
            }
        }

        if (Double.isInfinite(dist[t]))
            throw new IllegalStateException("No existe ruta entre origen y destino.");

        // Reconstrucción
        List<Integer> pathIdx = new ArrayList<>();
        for (int cur = t; cur != -1; cur = prev[cur]) pathIdx.add(cur);
        Collections.reverse(pathIdx);

        List<String> nombres = new ArrayList<>();
        for (int id : pathIdx) nombres.add(circuitos.get(id).nombre);

        return new Resultado(nombres, Math.round(dist[t]));
    }
}
