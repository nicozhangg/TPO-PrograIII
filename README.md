# 🏎️ Ruta F1 Backend

Aplicación backend desarrollada en Spring Boot para optimizar rutas del campeonato de Fórmula 1, implementando diferentes algoritmos de optimización para encontrar el recorrido más eficiente entre los circuitos del calendario 2025.

## 🚀 Características

- Implementación de múltiples algoritmos de optimización:
  - Vecino Más Cercano + optimización 2-opt
  - BFS (Búsqueda en Anchura)
  - Algoritmo de Dijkstra
  - Branch and Bound para TSP
- Interfaz visual interactiva para visualizar las rutas
- API RESTful documentada con Swagger
- Integración opcional con Neo4j para persistencia de datos
- Visualización de rutas en mapa interactivo

## 🛠️ Tecnologías

- **Backend**: Java 17 + Spring Boot 3.5.7
- **Base de datos**: Neo4j (opcional)
- **Documentación**: Swagger / OpenAPI
- **Frontend**: HTML, CSS, JavaScript

## 📋 Requisitos Previos

- Java JDK 17 o superior
- Maven 3.6+
- Neo4j

## ⚙️ Instalación y Ejecución

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/nicozhangg/TPO-PrograIII.git
   cd TPO-PrograIII
   ```

2. **Compilar y ejecutar**
   ```bash
   mvn spring-boot:run
   ```
   
   O generar el archivo JAR:
   ```bash
   mvn -q -DskipTests package
   java -jar target/ruta-f1-backend-0.0.1-SNAPSHOT.jar
   ```

3. **Acceder a la aplicación**
   - Frontend: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html

## 🔌 API Endpoints

- `GET /api/algoritmos/nearest` - Ejecuta el algoritmo del Vecino Más Cercano + 2-opt
- `GET /api/algoritmos/bfs` - Ejecuta el algoritmo BFS para encontrar rutas
- `GET /api/algoritmos/dijkstra` - Calcula la ruta más corta usando Dijkstra
- `GET /api/algoritmos/branchbound` - Implementa Branch and Bound para TSP

## 🗄️ Configuración Neo4j (Opcional)

1. Asegúrate de tener Neo4j instalado y ejecutándose
2. Descomenta y configura las propiedades en `src/main/resources/application.properties`:
   ```properties
   spring.neo4j.uri=bolt://localhost:7687
   spring.neo4j.authentication.username=neo4j
   spring.neo4j.authentication.password=tu_contraseña
   ```

Cargar la base de neo4j
// 1) Evitar duplicados por nombre
CREATE CONSTRAINT circuito_nombre_unique IF NOT EXISTS
FOR (c:Circuito) REQUIRE c.nombre IS UNIQUE;

// 2) Datos (24 GPs 2025)
WITH [
  {nombre:'Sakhir (Bahréin)', latitud:26.032, longitud:50.511},
  {nombre:'Jeddah (Arabia Saudita)', latitud:21.543, longitud:39.172},
  {nombre:'Melbourne (Australia)', latitud:-37.846, longitud:144.971},
  {nombre:'Suzuka (Japón)', latitud:34.843, longitud:136.541},
  {nombre:'Shanghai (China)', latitud:31.338, longitud:121.220},
  {nombre:'Miami (EE.UU.)', latitud:25.958, longitud:-80.239},
  {nombre:'Imola (Italia)', latitud:44.343, longitud:11.716},
  {nombre:'Monaco (Mónaco)', latitud:43.734, longitud:7.420},
  {nombre:'Montreal (Canadá)', latitud:45.504, longitud:-73.551},
  {nombre:'Barcelona (España)', latitud:41.570, longitud:2.261},
  {nombre:'Spielberg (Austria)', latitud:47.219, longitud:14.764},
  {nombre:'Silverstone (Reino Unido)', latitud:52.073, longitud:-1.014},
  {nombre:'Budapest (Hungría)', latitud:47.583, longitud:19.252},
  {nombre:'Spa-Francorchamps (Bélgica)', latitud:50.437, longitud:5.971},
  {nombre:'Zandvoort (Países Bajos)', latitud:52.388, longitud:4.540},
  {nombre:'Monza (Italia)', latitud:45.620, longitud:9.290},
  {nombre:'Baku (Azerbaiyán)', latitud:40.372, longitud:49.853},
  {nombre:'Singapore (Singapur)', latitud:1.291, longitud:103.863},
  {nombre:'Austin (EE.UU.)', latitud:30.132, longitud:-97.641},
  {nombre:'Mexico City (México)', latitud:19.404, longitud:-99.090},
  {nombre:'Sao Paulo (Brasil)', latitud:-23.701, longitud:-46.697},
  {nombre:'Las Vegas (EE.UU.)', latitud:36.174, longitud:-115.137},
  {nombre:'Lusail (Qatar)', latitud:25.332, longitud:51.575},
  {nombre:'Abu Dhabi (EAU)', latitud:24.469, longitud:54.603}
] AS data
UNWIND data AS row
MERGE (c:Circuito {nombre: row.nombre})
SET c.latitud = row.latitud,
    c.longitud = row.longitud;