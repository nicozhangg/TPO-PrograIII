package com.f1ruta.domain;

import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@Node("Circuito")
public class Circuito {

    @Id
    private String nombre;
    
    private double latitud;
    private double longitud;

    // Relaciones hacia otros circuitos (aristas con propiedades)
    @Relationship(type = "CONECTA_CON")
    private List<Conexion> conexiones;

    public Circuito(String nombre, double latitud, double longitud) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }
}
