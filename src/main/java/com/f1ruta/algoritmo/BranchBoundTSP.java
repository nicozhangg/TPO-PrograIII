package com.f1ruta.algoritmo;

import java.util.*;
import java.util.Locale;
import com.f1ruta.algoritmo.RutaF1TSP.Circuito;

public class BranchBoundTSP {

    public static record Resultado(List<String> ruta, double kmTotales) {}

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

    /** Nodo del árbol de búsqueda */
    private static class Nodo {
        List<Integer> ruta;          // Ruta parcial
        double costoActual;          // Costo acumulado
        double cotaInferior;         // Estimación del costo mínimo restante
        boolean[] visitado;          // Nodos visitados

        Nodo(List<Integer> ruta, double costoActual, double cotaInferior, boolean[] visitado) {
            this.ruta = new ArrayList<>(ruta);
            this.costoActual = costoActual;
            this.cotaInferior = cotaInferior;
            this.visitado = Arrays.copyOf(visitado, visitado.length);
        }

        double getCostoTotalEstimado() {
            return costoActual + cotaInferior;
        }
    }

    /** Calcula cota inferior usando la suma de las dos aristas más cortas de cada nodo */
    private static double calcularCotaInferior(double[][] distancias, boolean[] visitado, int ultimo) {
        int n = distancias.length;
        double cota = 0.0;
        int noVisitados = 0;
        
        // Contar nodos no visitados
        for (boolean v : visitado) {
            if (!v) noVisitados++;
        }
        
        if (noVisitados == 0) return 0.0;

        // Para el último nodo visitado: sumar la arista más corta hacia un nodo no visitado
        if (ultimo >= 0) {
            double minDesdeUltimo = Double.POSITIVE_INFINITY;
            for (int j = 0; j < n; j++) {
                if (!visitado[j]) {
                    minDesdeUltimo = Math.min(minDesdeUltimo, distancias[ultimo][j]);
                }
            }
            if (minDesdeUltimo != Double.POSITIVE_INFINITY) {
                cota += minDesdeUltimo;
            }
        }

        // Para cada nodo no visitado: sumar las dos aristas más cortas hacia otros nodos no visitados
        for (int i = 0; i < n; i++) {
            if (!visitado[i]) {
                double min1 = Double.POSITIVE_INFINITY;
                double min2 = Double.POSITIVE_INFINITY;
                
                for (int j = 0; j < n; j++) {
                    if (i != j && !visitado[j]) {
                        double dist = distancias[i][j];
                        if (dist < min1) {
                            min2 = min1;
                            min1 = dist;
                        } else if (dist < min2) {
                            min2 = dist;
                        }
                    }
                }
                
                if (min1 != Double.POSITIVE_INFINITY && min2 != Double.POSITIVE_INFINITY) {
                    cota += min1 + min2;
                } else if (min1 != Double.POSITIVE_INFINITY) {
                    // Si solo hay un nodo no visitado, usar la distancia dos veces
                    cota += min1 * 2;
                }
            }
        }

        // Dividir por 2 porque cada arista se cuenta dos veces (desde i y desde j)
        return cota / 2.0;
    }

    /** Encuentra la ruta óptima usando Branch & Bound con límites de tiempo y nodos */
    public static Resultado calcularRuta(List<Circuito> cs, String origenNombre) {
        int n = cs.size();
        if (n == 0) return new Resultado(List.of(), 0.0);
        if (n == 1) return new Resultado(List.of(cs.get(0).nombre), 0.0);

        double[][] distancias = distancias(cs);
        
        // Buscar índice del origen
        int origen = 0;
        if (origenNombre != null && !origenNombre.trim().isEmpty()) {
            origenNombre = origenNombre.trim().toLowerCase(Locale.ROOT);
            for (int i = 0; i < n; i++) {
                if (cs.get(i).nombre.toLowerCase(Locale.ROOT).equals(origenNombre)) {
                    origen = i;
                    break;
                }
            }
        }

        // Solución inicial usando Nearest Neighbor como cota superior
        List<Integer> mejorRuta = vecinoMasCercano(distancias, origen);
        double mejorCosto = calcularCostoRuta(distancias, mejorRuta);

        // Límites para evitar que tarde demasiado
        // Ajustar según el número de nodos: más nodos = límites más estrictos
        long tiempoInicio = System.currentTimeMillis();
        long tiempoLimite;
        int maxNodos;
        
        if (n > 20) {
            tiempoLimite = 3000; // 3 segundos para casos grandes
            maxNodos = 15000;
        } else if (n > 15) {
            tiempoLimite = 5000; // 5 segundos
            maxNodos = 30000;
        } else {
            tiempoLimite = 10000; // 10 segundos para casos pequeños
            maxNodos = 100000;
        }
        
        int nodosExplorados = 0;

        // Cola de prioridad (explorar nodos con menor cota primero)
        PriorityQueue<Nodo> cola = new PriorityQueue<>(
            Comparator.comparingDouble(Nodo::getCostoTotalEstimado)
        );

        // Nodo inicial
        boolean[] visitadoInicial = new boolean[n];
        visitadoInicial[origen] = true;
        List<Integer> rutaInicial = new ArrayList<>();
        rutaInicial.add(origen);
        
        double cotaInicial = calcularCotaInferior(distancias, visitadoInicial, origen);
        cola.offer(new Nodo(rutaInicial, 0.0, cotaInicial, visitadoInicial));

        // Branch & Bound con límites
        while (!cola.isEmpty()) {
            // Verificar límites de tiempo y nodos
            long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;
            if (tiempoTranscurrido > tiempoLimite || nodosExplorados > maxNodos) {
                break; // Usar la mejor solución encontrada hasta ahora
            }

            Nodo actual = cola.poll();
            nodosExplorados++;

            // Poda: si la cota inferior es mayor que la mejor solución, descartar
            if (actual.getCostoTotalEstimado() >= mejorCosto) {
                continue;
            }

            // Si ya visitamos todos los nodos, es una solución completa
            if (actual.ruta.size() == n) {
                double costo = actual.costoActual;
                if (costo < mejorCosto) {
                    mejorCosto = costo;
                    mejorRuta = new ArrayList<>(actual.ruta);
                }
                continue;
            }

            // Expandir: agregar cada nodo no visitado, ordenados por distancia
            int ultimo = actual.ruta.get(actual.ruta.size() - 1);
            
            // Ordenar nodos no visitados por distancia (heurística: explorar los más cercanos primero)
            List<int[]> candidatos = new ArrayList<>();
            for (int siguiente = 0; siguiente < n; siguiente++) {
                if (!actual.visitado[siguiente]) {
                    candidatos.add(new int[]{siguiente, (int)Math.round(distancias[ultimo][siguiente])});
                }
            }
            candidatos.sort(Comparator.comparingInt(a -> a[1]));
            
            // Expandir solo los mejores candidatos si hay muchos (heurística: explorar solo los más cercanos)
            // Ajustar según el número de nodos: más nodos = menos expansión
            int limiteExpansion;
            if (n > 20) {
                limiteExpansion = Math.min(candidatos.size(), 4); // Solo 4 mejores para casos grandes
            } else if (n > 15) {
                limiteExpansion = Math.min(candidatos.size(), 5);
            } else if (n > 10) {
                limiteExpansion = Math.min(candidatos.size(), 7);
            } else {
                limiteExpansion = candidatos.size(); // Todos para casos pequeños
            }
            
            for (int idx = 0; idx < limiteExpansion; idx++) {
                int siguiente = candidatos.get(idx)[0];
                double nuevoCosto = actual.costoActual + distancias[ultimo][siguiente];
                
                // Crear nuevo nodo
                List<Integer> nuevaRuta = new ArrayList<>(actual.ruta);
                nuevaRuta.add(siguiente);
                boolean[] nuevoVisitado = Arrays.copyOf(actual.visitado, n);
                nuevoVisitado[siguiente] = true;
                
                double nuevaCota = calcularCotaInferior(distancias, nuevoVisitado, siguiente);
                double costoEstimado = nuevoCosto + nuevaCota;
                
                // Poda: solo agregar si puede mejorar la mejor solución
                if (costoEstimado < mejorCosto) {
                    cola.offer(new Nodo(nuevaRuta, nuevoCosto, nuevaCota, nuevoVisitado));
                }
            }
        }

        // Convertir índices a nombres
        List<String> nombres = new ArrayList<>();
        for (int idx : mejorRuta) {
            nombres.add(cs.get(idx).nombre);
        }

        return new Resultado(nombres, Math.round(mejorCosto));
    }

    /** Heurística Nearest Neighbor para cota superior inicial */
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
            for (int j = 0; j < n; j++) {
                if (!vis[j] && d[cur][j] < bestD) {
                    bestD = d[cur][j];
                    best = j;
                }
            }
            r.add(best);
            vis[best] = true;
            cur = best;
        }
        return r;
    }

    /** Calcula el costo total de una ruta */
    private static double calcularCostoRuta(double[][] d, List<Integer> ruta) {
        double total = 0.0;
        for (int i = 0; i + 1 < ruta.size(); i++) {
            total += d[ruta.get(i)][ruta.get(i + 1)];
        }
        return total;
    }
}

