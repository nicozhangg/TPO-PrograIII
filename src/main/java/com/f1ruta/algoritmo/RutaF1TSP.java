package com.f1ruta.algoritmo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * TSP heurístico: Vecino Más Cercano + mejora 2-opt.
 * Usa distancia Haversine entre TODOS los pares (no necesita "conexiones" en el JSON).
 */
public class RutaF1TSP {

    /** Resultado del algoritmo */
    public static record Resultado(List<String> ruta, double kmTotales) {}

    /** DTO simple que coincide con el JSON (sin conexiones) */
    public static class Circuito {
        public String nombre;
        public double latitud;
        public double longitud;
    }

    /** Distancia Haversine en km */
    private static double haversine(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371.0;
        double p1 = Math.toRadians(lat1), p2 = Math.toRadians(lat2);
        double dphi = p2 - p1;
        double dlambda = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dphi / 2) * Math.sin(dphi / 2)
                + Math.cos(p1) * Math.cos(p2) * Math.sin(dlambda / 2) * Math.sin(dlambda / 2);
        return 2 * R * Math.asin(Math.sqrt(a));
    }

    /** Matriz de distancias entre todos los circuitos */
    private static double[][] distancias(List<Circuito> cs) {
        int n = cs.size();
        double[][] d = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                d[i][j] = (i == j) ? 0.0 :
                        haversine(cs.get(i).latitud, cs.get(i).longitud,
                                  cs.get(j).latitud, cs.get(j).longitud);
            }
        }
        return d;
    }

    /** Longitud total (km) de una ruta dada la matriz de distancias */
    private static double largoRuta(double[][] d, List<Integer> ruta) {
        double total = 0;
        for (int i = 0; i + 1 < ruta.size(); i++) {
            total += d[ruta.get(i)][ruta.get(i + 1)];
        }
        return total;
    }

    /** Heurística: Vecino Más Cercano desde un inicio */
    private static List<Integer> vecinoMasCercano(double[][] d, int start) {
        int n = d.length;
        boolean[] vis = new boolean[n];
        List<Integer> r = new ArrayList<>(n);
        int cur = start;
        r.add(cur);
        vis[cur] = true;

        for (int k = 1; k < n; k++) {
            int best = -1;
            double bestD = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++) if (!vis[j]) {
                double dj = d[cur][j];
                if (dj < bestD) { bestD = dj; best = j; }
            }
            r.add(best);
            vis[best] = true;
            cur = best;
        }
        return r;
    }

    /** Mejora local 2-opt */
    private static List<Integer> dosOpt(double[][] d, List<Integer> ruta) {
        List<Integer> best = new ArrayList<>(ruta);
        boolean mejora = true;
        int n = best.size();

        while (mejora) {
            mejora = false;
            for (int i = 1; i < n - 2; i++) {
                for (int j = i + 1; j < n - 1; j++) {
                    int a = best.get(i - 1), b = best.get(i);
                    int c = best.get(j), e = best.get(j + 1);
                    double delta = (d[a][c] + d[b][e]) - (d[a][b] + d[c][e]);
                    if (delta < -1e-6) {
                        // invertir el segmento [i, j]
                        Collections.reverse(best.subList(i, j + 1));
                        mejora = true;
                    }
                }
            }
        }
        return best;
    }

    /** Ejecuta el TSP heurístico sobre la lista de circuitos (ruta abierta, no vuelve al inicio) */
    public static Resultado calcularRuta(List<Circuito> circuitos) {
        if (circuitos == null || circuitos.isEmpty()) {
            return new Resultado(List.of(), 0);
        }
        
        double[][] d = distancias(circuitos);
        int n = circuitos.size();
        List<Integer> mejor = vecinoMasCercano(d, 0); // Inicializar con primer circuito
        double mejorL = largoRuta(d, mejor);

        for (int s = 1; s < n; s++) {
            List<Integer> nn = vecinoMasCercano(d, s);
            List<Integer> opt = dosOpt(d, nn);
            double L = largoRuta(d, opt);
            if (L < mejorL) { 
                mejorL = L; 
                mejor = opt; 
            }
        }

        List<String> nombres = new ArrayList<>();
        for (int idx : mejor) {
            nombres.add(circuitos.get(idx).nombre);
        }
        return new Resultado(nombres, Math.round(mejorL));
    }
}
