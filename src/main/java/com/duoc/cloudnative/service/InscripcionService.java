package com.duoc.cloudnative.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.duoc.cloudnative.dto.CursoInscritoDTO;
import com.duoc.cloudnative.dto.InscripcionRequestDTO;
import com.duoc.cloudnative.dto.InscripcionResponseDTO;
import com.duoc.cloudnative.entity.Curso;
import com.duoc.cloudnative.entity.DetalleInscripcion;
import com.duoc.cloudnative.entity.Estudiante;
import com.duoc.cloudnative.entity.Inscripcion;
import com.duoc.cloudnative.repository.EstudianteRepository;
import com.duoc.cloudnative.repository.InscripcionRepository;

import jakarta.transaction.Transactional;

@Service
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final EstudianteRepository estudianteRepository;
    private final CursoService cursoService;
    private final ProductorService productorService;

    public InscripcionService(InscripcionRepository inscripcionRepository,
                              EstudianteRepository estudianteRepository,
                              CursoService cursoService,
                              ProductorService productorService) {
        this.inscripcionRepository = inscripcionRepository;
        this.estudianteRepository = estudianteRepository;
        this.cursoService = cursoService;
        this.productorService = productorService;
    }

    // Registra una inscripción completa: estudiante, cursos seleccionados, detalles y total a pagar.
    @Transactional
    public InscripcionResponseDTO registrarInscripcion(InscripcionRequestDTO requestDTO) {

        String nombreNormalizado = requestDTO.getNombreEstudiante().trim();
        String correoNormalizado = requestDTO.getCorreoEstudiante().trim().toLowerCase();

        // Evita IDs repetidos en la solicitud de inscripción.
        List<Long> idsCursosUnicos = requestDTO.getIdsCursos()
                .stream()
                .distinct()
                .toList();

        if (idsCursosUnicos.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos un curso.");
        }

        // Si el estudiante ya existe, se reutiliza. Si no existe, se crea uno nuevo.
        Estudiante estudiante = estudianteRepository.findByCorreoIgnoreCase(correoNormalizado)
                .orElseGet(() -> {
                    Estudiante nuevoEstudiante = new Estudiante();
                    nuevoEstudiante.setNombre(nombreNormalizado);
                    nuevoEstudiante.setCorreo(correoNormalizado);
                    return estudianteRepository.save(nuevoEstudiante);
                });

        // Busca los cursos seleccionados. Si uno no existe, el CursoService lanzará un error.
        List<Curso> cursosSeleccionados = cursoService.obtenerCursosPorIds(idsCursosUnicos);

        // Calcula el total sumando el costo de todos los cursos seleccionados.
        BigDecimal total = cursosSeleccionados.stream()
                .map(Curso::getCosto)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Inscripcion inscripcion = new Inscripcion();
        inscripcion.setEstudiante(estudiante);
        inscripcion.setFechaInscripcion(LocalDateTime.now());
        inscripcion.setTotal(total);

        // Crea un detalle por cada curso seleccionado.
        for (Curso curso : cursosSeleccionados) {
            DetalleInscripcion detalle = new DetalleInscripcion();
            detalle.setInscripcion(inscripcion);
            detalle.setCurso(curso);
            detalle.setCostoCurso(curso.getCosto());

            inscripcion.getDetalles().add(detalle);
        }

        // Guarda la inscripción completa. Los detalles se guardan por CascadeType.ALL.
        Inscripcion inscripcionGuardada = inscripcionRepository.save(inscripcion);

        // Convierte los detalles guardados en DTO para entregar un resumen claro al cliente.
        List<CursoInscritoDTO> cursosInscritos = inscripcionGuardada.getDetalles()
                .stream()
                .map(detalle -> new CursoInscritoDTO(
                        detalle.getCurso().getId(),
                        detalle.getCurso().getNombre(),
                        detalle.getCurso().getInstructor(),
                        detalle.getCostoCurso()
                ))
                .toList();

        // Construye la respuesta y publica la inscripción en RabbitMQ.
        InscripcionResponseDTO response = new InscripcionResponseDTO(
                inscripcionGuardada.getId(),
                estudiante.getNombre(),
                estudiante.getCorreo(),
                inscripcionGuardada.getFechaInscripcion(),
                cursosInscritos,
                inscripcionGuardada.getTotal()
        );

        productorService.enviarMensaje(response);

        return response;
    }
}