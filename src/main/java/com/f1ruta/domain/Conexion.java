package com.f1ruta.domain;

import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@RelationshipProperties
public class Conexion {

    @Id
    @GeneratedValue
    private Long id; // 🔹 requerido por Spring Data Neo4j para actualizar relaciones con propiedades

    private double distancia; // propiedad de la relación (peso, km)

    @TargetNode
    private Circuito destino;

    public Conexion(Circuito destino, double distancia) {
        this.destino = destino;
        this.distancia = distancia;
    }
}
