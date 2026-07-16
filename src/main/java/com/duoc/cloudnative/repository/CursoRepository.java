package com.duoc.cloudnative.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.cloudnative.entity.Curso;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    // Permite buscar cursos por coincidencia parcial del nombre, ignorando mayúsculas y minúsculas.
    List<Curso> findByNombreContainingIgnoreCase(String nombre);

    // Permite validar si ya existe un curso con el mismo nombre.
    boolean existsByNombreIgnoreCase(String nombre);
}