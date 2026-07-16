package com.duoc.cloudnative.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.duoc.cloudnative.dto.CursoRequestDTO;
import com.duoc.cloudnative.entity.Curso;
import com.duoc.cloudnative.repository.CursoRepository;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    // Lista todos los cursos registrados en la base de datos.
    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    // Guarda un nuevo curso, validando previamente que no exista otro con el mismo nombre.
    public Curso agregarCurso(CursoRequestDTO cursoRequestDTO) {

        String nombreNormalizado = cursoRequestDTO.getNombre().trim();
        String instructorNormalizado = cursoRequestDTO.getInstructor().trim();

        if (cursoRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new IllegalArgumentException("Ya existe un curso registrado con el nombre: " + nombreNormalizado);
        }

        Curso curso = new Curso();
        curso.setNombre(nombreNormalizado);
        curso.setInstructor(instructorNormalizado);
        curso.setDuracionHoras(cursoRequestDTO.getDuracionHoras());
        curso.setCosto(cursoRequestDTO.getCosto());

        return cursoRepository.save(curso);
    }

    // Busca un curso específico por su ID. Será útil para las inscripciones.
    public Curso obtenerCursoPorId(Long id) {

        Long idValidado = Objects.requireNonNull(id, "El ID del curso no puede ser nulo.");

        return cursoRepository.findById(idValidado)
                .orElseThrow(() -> new IllegalArgumentException("No existe un curso con el ID: " + idValidado));
    }

    // Busca cursos por coincidencia parcial del nombre.
    public List<Curso> buscarCursosPorNombre(String nombre) {
        return cursoRepository.findByNombreContainingIgnoreCase(nombre);
    }

    // Busca varios cursos por sus IDs. Será necesario para inscribir a un estudiante en uno o más cursos.
    public List<Curso> obtenerCursosPorIds(List<Long> idsCursos) {

        if (idsCursos == null || idsCursos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un curso.");
        }

        if (idsCursos.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("La lista de cursos no puede contener IDs nulos.");
        }

        List<Long> idsCursosUnicos = idsCursos
                .stream()
                .distinct()
                .toList();

        List<Long> idsValidados = Objects.requireNonNull(idsCursosUnicos, "La lista de cursos no puede ser nula.");

        List<Curso> cursosEncontrados = cursoRepository.findAllById(idsValidados);

        if (cursosEncontrados.size() != idsValidados.size()) {
            throw new IllegalArgumentException("Uno o más cursos seleccionados no existen en el sistema.");
        }

        return cursosEncontrados;
    }
}