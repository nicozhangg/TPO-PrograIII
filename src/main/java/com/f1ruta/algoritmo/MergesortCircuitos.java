package com.f1ruta.algoritmo;

import java.util.*;
import com.f1ruta.algoritmo.RutaF1TSP.Circuito;

/**
 * Algoritmo Divide y Vencerás: Mergesort para ordenar circuitos
 * Permite ordenar por diferentes criterios: latitud, longitud, nombre
 */
public class MergesortCircuitos {

    public static record Resultado(
            List<Map<String, Object>> circuitosOrdenados,
            String criterio,
            String orden,
            int cantidadCircuitos
    ) {}

    /**
     * Criterios de ordenamiento disponibles
     */
    public enum Criterio {
        LATITUD,      // De sur a norte (menor a mayor latitud)
        LONGITUD,     // De oeste a este (menor a mayor longitud)
        NOMBRE        // Alfabético
    }

    /**
     * Orden de clasificación
     */
    public enum Orden {
        ASC,  // Ascendente
        DESC  // Descendente
    }

    /**
     * Ordena la lista de circuitos usando Mergesort
     */
    public static Resultado ordenar(
            List<Circuito> circuitos,
            String criterioStr,
            String ordenStr
    ) {
        if (circuitos == null || circuitos.isEmpty()) {
            return new Resultado(List.of(), criterioStr, ordenStr, 0);
        }

        // Parsear criterio y orden
        Criterio criterio;
        try {
            criterio = Criterio.valueOf(criterioStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            criterio = Criterio.NOMBRE; // Default
        }

        Orden orden;
        try {
            orden = Orden.valueOf(ordenStr.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            orden = Orden.ASC; // Default
        }

        // Crear lista mutable para ordenar
        List<Circuito> lista = new ArrayList<>(circuitos);

        // Ejecutar Mergesort según criterio
        switch (criterio) {
            case LATITUD -> mergesort(lista, orden, (c1, c2) -> Double.compare(c1.latitud, c2.latitud));
            case LONGITUD -> mergesort(lista, orden, (c1, c2) -> Double.compare(c1.longitud, c2.longitud));
            case NOMBRE -> mergesort(lista, orden, (c1, c2) -> c1.nombre.compareToIgnoreCase(c2.nombre));
        }

        // Convertir a formato de respuesta
        List<Map<String, Object>> resultado = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            Circuito c = lista.get(i);
            Map<String, Object> mapa = new LinkedHashMap<>();
            mapa.put("posicion", i + 1);
            mapa.put("nombre", c.nombre);
            mapa.put("latitud", c.latitud);
            mapa.put("longitud", c.longitud);
            resultado.add(mapa);
        }

        return new Resultado(resultado, criterioStr, ordenStr, lista.size());
    }

    // ==================== MERGESORT GENÉRICO ====================

    @FunctionalInterface
    private interface Comparador {
        int comparar(Circuito c1, Circuito c2);
    }

    private static void mergesort(List<Circuito> lista, Orden orden, Comparador comparador) {
        if (lista.size() <= 1) return;
        mergesortRecursivo(lista, 0, lista.size() - 1, orden, comparador);
    }

    private static void mergesortRecursivo(List<Circuito> lista, int inicio, int fin, Orden orden, Comparador comparador) {
        if (inicio >= fin) return;

        int medio = inicio + (fin - inicio) / 2;

        mergesortRecursivo(lista, inicio, medio, orden, comparador);
        mergesortRecursivo(lista, medio + 1, fin, orden, comparador);

        merge(lista, inicio, medio, fin, orden, comparador);
    }

    private static void merge(List<Circuito> lista, int inicio, int medio, int fin, Orden orden, Comparador comparador) {
        List<Circuito> izq = new ArrayList<>(lista.subList(inicio, medio + 1));
        List<Circuito> der = new ArrayList<>(lista.subList(medio + 1, fin + 1));

        int i = 0, j = 0, k = inicio;

        while (i < izq.size() && j < der.size()) {
            int comparacion = comparador.comparar(izq.get(i), der.get(j));
            boolean condicion = (orden == Orden.ASC) ? comparacion <= 0 : comparacion >= 0;

            if (condicion) {
                lista.set(k++, izq.get(i++));
            } else {
                lista.set(k++, der.get(j++));
            }
        }

        while (i < izq.size()) lista.set(k++, izq.get(i++));
        while (j < der.size()) lista.set(k++, der.get(j++));
    }
}
