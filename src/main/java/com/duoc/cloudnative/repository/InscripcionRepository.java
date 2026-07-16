package com.duoc.cloudnative.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.cloudnative.entity.Inscripcion;

public interface InscripcionRepository extends JpaRepository<Inscripcion, Long> {

    // Permite buscar todas las inscripciones asociadas a un correo de estudiante.
    List<Inscripcion> findByEstudianteCorreoIgnoreCase(String correo);
}