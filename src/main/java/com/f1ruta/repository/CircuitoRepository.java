package com.f1ruta.repository;

import com.f1ruta.domain.Circuito;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CircuitoRepository extends Neo4jRepository<Circuito, String> {
    
    Optional<Circuito> findByNombre(String nombre);
    
    List<Circuito> findAll();
}

