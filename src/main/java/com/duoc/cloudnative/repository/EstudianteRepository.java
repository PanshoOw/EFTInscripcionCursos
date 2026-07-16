package com.duoc.cloudnative.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.cloudnative.entity.Estudiante;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    // Busca un estudiante mediante su correo, ignorando mayúsculas y minúsculas.
    Optional<Estudiante> findByCorreoIgnoreCase(String correo);

    // Permite verificar si ya existe un estudiante registrado con ese correo.
    boolean existsByCorreoIgnoreCase(String correo);
}