package com.duoc.cloudnative.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.duoc.cloudnative.entity.Curso;

public interface CursoRepository
        extends JpaRepository<Curso, Long> {

    /**
     * Busca cursos cuyo nombre contenga el texto indicado,
     * ignorando diferencias entre mayúsculas y minúsculas.
     */
    List<Curso> findByNombreContainingIgnoreCase(
            String nombre
    );

    /**
     * Comprueba si ya existe un curso con el nombre indicado.
     */
    boolean existsByNombreIgnoreCase(
            String nombre
    );

    /**
     * Comprueba si otro curso utiliza el mismo nombre,
     * excluyendo el registro que se está actualizando.
     */
    boolean existsByNombreIgnoreCaseAndIdNot(
            String nombre,
            Long id
    );
}