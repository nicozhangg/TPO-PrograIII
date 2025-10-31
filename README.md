# Ruta F1 Backend (Spring Boot)

API en **Java + Spring Boot 3.5.7** para ejecutar algoritmos de optimización de ruta F1.

## Endpoints
- `GET /api/algoritmos/nearest` → Ejecuta **Vecino Más Cercano + 2-opt** y devuelve el orden propuesto y la distancia total.
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`

## Ejecutar
```bash
mvn spring-boot:run
```
o empaquetado:
```bash
mvn -q -DskipTests package
java -jar target/ruta-f1-backend-0.0.1-SNAPSHOT.jar
```

## Configurar Neo4j (opcional)
Descomentar propiedades en `src/main/resources/application.properties` y asegurarte que el servidor Neo4j esté activo.

## Ver mapa
http://localhost:8080