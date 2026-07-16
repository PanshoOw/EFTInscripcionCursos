package com.duoc.cloudnative.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.cloudnative.entity.DetalleInscripcion;

public interface DetalleInscripcionRepository extends JpaRepository<DetalleInscripcion, Long> {

    // Permite buscar todos los detalles asociados a una inscripción específica.
    List<DetalleInscripcion> findByInscripcionId(Long inscripcionId);
}