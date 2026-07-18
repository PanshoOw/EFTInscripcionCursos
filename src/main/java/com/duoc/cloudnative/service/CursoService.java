package com.duoc.cloudnative.service;

import java.util.List;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.cloudnative.dto.CursoRequestDTO;
import com.duoc.cloudnative.entity.Curso;
import com.duoc.cloudnative.repository.CursoRepository;

@Service
public class CursoService {

    private static final String MENSAJE_ID_NULO =
            "El ID del curso no puede ser nulo.";

    private final CursoRepository cursoRepository;

    public CursoService(
            CursoRepository cursoRepository
    ) {
        this.cursoRepository = cursoRepository;
    }

    /**
     * Lista todos los cursos registrados.
     */
    @Transactional(readOnly = true)
    public List<Curso> listarCursos() {
        return cursoRepository.findAll();
    }

    /**
     * Registra un nuevo curso, evitando nombres duplicados.
     */
    @Transactional
    public Curso agregarCurso(
            CursoRequestDTO cursoRequestDTO
    ) {

        CursoRequestDTO solicitud =
                validarSolicitud(cursoRequestDTO);

        String nombreNormalizado =
                normalizarTexto(
                        solicitud.getNombre(),
                        "El nombre del curso es obligatorio."
                );

        String instructorNormalizado =
                normalizarTexto(
                        solicitud.getInstructor(),
                        "El nombre del instructor es obligatorio."
                );

        if (cursoRepository.existsByNombreIgnoreCase(
                nombreNormalizado
        )) {

            throw new IllegalArgumentException(
                    "Ya existe un curso registrado con el nombre: "
                            + nombreNormalizado
            );
        }

        Curso curso = new Curso();

        curso.setNombre(nombreNormalizado);
        curso.setInstructor(instructorNormalizado);
        curso.setDuracionHoras(
                solicitud.getDuracionHoras()
        );
        curso.setCosto(
                solicitud.getCosto()
        );

        return cursoRepository.save(curso);
    }

    /**
     * Actualiza los datos de un curso existente.
     */
    @Transactional
    public Curso actualizarCurso(
            Long id,
            CursoRequestDTO cursoRequestDTO
    ) {

        Long idValidado = validarId(id);

        CursoRequestDTO solicitud =
                validarSolicitud(cursoRequestDTO);

        Curso cursoExistente =
                obtenerCursoPorId(idValidado);

        String nombreNormalizado =
                normalizarTexto(
                        solicitud.getNombre(),
                        "El nombre del curso es obligatorio."
                );

        String instructorNormalizado =
                normalizarTexto(
                        solicitud.getInstructor(),
                        "El nombre del instructor es obligatorio."
                );

        boolean nombreUtilizadoPorOtroCurso =
                cursoRepository
                        .existsByNombreIgnoreCaseAndIdNot(
                                nombreNormalizado,
                                idValidado
                        );

        if (nombreUtilizadoPorOtroCurso) {

            throw new IllegalArgumentException(
                    "Ya existe otro curso registrado con el nombre: "
                            + nombreNormalizado
            );
        }

        cursoExistente.setNombre(
                nombreNormalizado
        );

        cursoExistente.setInstructor(
                instructorNormalizado
        );

        cursoExistente.setDuracionHoras(
                solicitud.getDuracionHoras()
        );

        cursoExistente.setCosto(
                solicitud.getCosto()
        );

        return cursoRepository.save(cursoExistente);
    }

    /**
     * Elimina un curso existente.
     *
     * La eliminación puede ser rechazada por Oracle cuando
     * el curso ya se encuentra asociado a una inscripción.
     */
    @Transactional
    public void eliminarCurso(Long id) {

        Long idValidado = validarId(id);

        Curso cursoExistente =
                obtenerCursoPorId(idValidado);

        cursoRepository.delete(cursoExistente);
    }

    /**
     * Busca un curso específico por su identificador.
     */
    @Transactional(readOnly = true)
    public Curso obtenerCursoPorId(Long id) {

        Long idValidado = validarId(id);

        return cursoRepository
                .findById(idValidado)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "No existe un curso con el ID: "
                                        + idValidado
                        )
                );
    }

    /**
     * Busca cursos por coincidencia parcial del nombre.
     */
    @Transactional(readOnly = true)
    public List<Curso> buscarCursosPorNombre(
            String nombre
    ) {

        String nombreNormalizado =
                normalizarTexto(
                        nombre,
                        "El nombre de búsqueda es obligatorio."
                );

        return cursoRepository
                .findByNombreContainingIgnoreCase(
                        nombreNormalizado
                );
    }

    /**
     * Busca varios cursos por sus identificadores.
     */
    @Transactional(readOnly = true)
    public List<Curso> obtenerCursosPorIds(
            List<Long> idsCursos
    ) {

        if (idsCursos == null
                || idsCursos.isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe seleccionar al menos un curso."
            );
        }

        if (idsCursos.stream()
                .anyMatch(Objects::isNull)) {

            throw new IllegalArgumentException(
                    "La lista de cursos no puede contener IDs nulos."
            );
        }

        List<Long> idsCursosUnicos =
                idsCursos.stream()
                        .distinct()
                        .toList();

        List<Curso> cursosEncontrados =
                cursoRepository.findAllById(
                        idsCursosUnicos
                );

        if (cursosEncontrados.size()
                != idsCursosUnicos.size()) {

            throw new IllegalArgumentException(
                    "Uno o más cursos seleccionados "
                            + "no existen en el sistema."
            );
        }

        return cursosEncontrados;
    }

    private Long validarId(Long id) {

        return Objects.requireNonNull(
                id,
                MENSAJE_ID_NULO
        );
    }

    private CursoRequestDTO validarSolicitud(
            CursoRequestDTO cursoRequestDTO
    ) {

        return Objects.requireNonNull(
                cursoRequestDTO,
                "Los datos del curso no pueden ser nulos."
        );
    }

    private String normalizarTexto(
            String valor,
            String mensajeError
    ) {

        if (valor == null
                || valor.isBlank()) {

            throw new IllegalArgumentException(
                    mensajeError
            );
        }

        return valor.trim();
    }
}