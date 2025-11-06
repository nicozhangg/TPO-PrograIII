# 🔄 Algoritmo Mergesort (Divide y Vencerás)

## 📝 Descripción

Implementación del algoritmo **Mergesort** para ordenar los 24 circuitos de F1 según diferentes criterios. Este algoritmo pertenece a la categoría **Divide y Vencerás**.

### Complejidad
- **Temporal:** O(n log n) en todos los casos (mejor, promedio y peor)
- **Espacial:** O(n) por las copias en el merge

---

## 🎯 Criterios de Ordenamiento Disponibles

### 1. **Por Nombre** (alfabético)
```
http://localhost:8080/api/algoritmos/mergesort?criterio=nombre&orden=asc
```
**Ejemplo de resultado:**
- Abu Dhabi (EAU)
- Austin (EE.UU.)
- Baku (Azerbaiyán)
- ...

### 2. **Por Latitud** (Sur → Norte)
```
http://localhost:8080/api/algoritmos/mergesort?criterio=latitud&orden=asc
```
**Ejemplo de resultado:**
- Melbourne (Australia) → -37.846°
- Sao Paulo (Brasil) → -23.701°
- Singapore (Singapur) → 1.291°
- ...

### 3. **Por Longitud** (Oeste → Este)
```
http://localhost:8080/api/algoritmos/mergesort?criterio=longitud&orden=asc
```
**Ejemplo de resultado:**
- Las Vegas (EE.UU.) → -115.137°
- Austin (EE.UU.) → -97.641°
- Miami (EE.UU.) → -80.239°
- ...

---

## 🚀 Ejemplos de Uso

### Ordenar alfabéticamente (A-Z)
```bash
curl "http://localhost:8080/api/algoritmos/mergesort?criterio=nombre&orden=asc"
```

### Ordenar de Sur a Norte
```bash
curl "http://localhost:8080/api/algoritmos/mergesort?criterio=latitud&orden=asc"
```

### Ordenar de Norte a Sur
```bash
curl "http://localhost:8080/api/algoritmos/mergesort?criterio=latitud&orden=desc"
```

---

## 📊 Respuesta de la API

```json
{
  "algoritmo": "Mergesort (Divide y Vencerás)",
  "criterio": "latitud",
  "orden": "asc",
  "cantidad_circuitos": 24,
  "km_totales": 98543,
  "circuitos_ordenados": [
    {
      "posicion": 1,
      "nombre": "Melbourne (Australia)",
      "latitud": -37.846,
      "longitud": 144.971
    },
    {
      "posicion": 2,
      "nombre": "Sao Paulo (Brasil)",
      "latitud": -23.701,
      "longitud": -46.697
    },
    ...
  ],
  "puntos_ordenados": [
    {
      "nombre": "Melbourne (Australia)",
      "lat": -37.846,
      "lon": 144.971
    },
    ...
  ]
}
```

**Nota:** El campo `km_totales` muestra la distancia total del recorrido siguiendo el orden resultante del algoritmo (suma de distancias entre circuitos consecutivos).

---

## 🎨 Interfaz Web

En el navegador (`http://localhost:8080`):

1. Selecciona **"Mergesort (Ordenar)"** en el dropdown
2. Elige el criterio:
   - Por Nombre (alfabético)
   - Por Latitud (Sur → Norte)
   - Por Longitud (Oeste → Este)
3. Selecciona orden: Ascendente o Descendente
4. Haz clic en **"Ejecutar"**

Los circuitos aparecerán:
- **Numerados en el mapa** según el orden resultante (1, 2, 3...)
- **Conectados con líneas naranjas** mostrando el recorrido en ese orden
- **Con información detallada** en los popups (posición, coordenadas)
- **Distancia total del recorrido** en el popup principal

---

## 🧮 Cómo Funciona el Algoritmo

### Principio: Divide y Vencerás

```
1. DIVIDIR: Partir la lista por la mitad recursivamente hasta tener sublistas de 1 elemento
2. CONQUISTAR: Mezclar (merge) las sublistas ordenadas de forma ascendente/descendente
3. RESULTADO: Lista completamente ordenada
```

### Ejemplo visual (ordenar por latitud):

```
Inicial: [Monaco(43.7), Singapore(1.3), Melbourne(-37.8), Barcelona(41.5)]

DIVIDIR:
  [Monaco(43.7), Singapore(1.3)]  |  [Melbourne(-37.8), Barcelona(41.5)]
  
  [Monaco(43.7)] [Singapore(1.3)]  |  [Melbourne(-37.8)] [Barcelona(41.5)]

MERGE (ASC):
  [Singapore(1.3), Monaco(43.7)]  |  [Melbourne(-37.8), Barcelona(41.5)]
  
  [Melbourne(-37.8), Singapore(1.3), Barcelona(41.5), Monaco(43.7)]
```

---

## ✅ Ventajas del Mergesort

- ✅ **Estable:** Mantiene el orden relativo de elementos iguales
- ✅ **Predecible:** Siempre O(n log n), sin peor caso cuadrático
- ✅ **Paralelizable:** Ideal para ordenar grandes datasets
- ✅ **Divide y Vencerás:** Ejemplo clásico de esta técnica

---

## 📚 Comparación con otros algoritmos de ordenamiento

| Algoritmo | Mejor caso | Promedio | Peor caso | Espacio |
|-----------|-----------|----------|-----------|---------|
| **Mergesort** | O(n log n) | O(n log n) | O(n log n) | O(n) |
| Quicksort | O(n log n) | O(n log n) | O(n²) | O(log n) |
| Heapsort | O(n log n) | O(n log n) | O(n log n) | O(1) |
| Bubble Sort | O(n) | O(n²) | O(n²) | O(1) |

---

## 🎓 Preguntas Frecuentes del TP

### **¿Por qué Mergesort es Divide y Vencerás?**
> Divide el problema (ordenar n elementos) en subproblemas más pequeños (ordenar n/2 elementos), resuelve cada subproblema recursivamente y combina las soluciones (merge).

### **¿Cuándo usar Mergesort vs Quicksort?**
> - **Mergesort:** Cuando necesitas estabilidad o tiempo garantizado O(n log n)
> - **Quicksort:** Cuando el espacio es limitado y el peor caso es improbable

### **¿Cuál es la diferencia entre criterios de ordenamiento?**
> - **Latitud:** Ordena geográficamente de Sur a Norte (hemisferio)
> - **Longitud:** Ordena de Oeste a Este (husos horarios)
> - **Distancia:** Ordena por proximidad a un punto específico (útil para logística)

---

## 🔗 Documentación Swagger

Accede a la documentación interactiva:
```
http://localhost:8080/swagger-ui.html
```

Busca el endpoint: **GET /api/algoritmos/mergesort**

---

## ✨ Casos de Uso Prácticos

1. **Planificación de temporada:** Ordenar por latitud para minimizar cambios climáticos
2. **Logística:** Ordenar por distancia desde centro de operaciones
3. **Análisis geográfico:** Agrupar circuitos por región (longitud)
4. **Presentaciones:** Orden alfabético para reportes

---

**Implementado el:** 6 de noviembre de 2025  
**Complejidad:** O(n log n)  
**Categoría del TP:** Divide y Vencerás ✅
