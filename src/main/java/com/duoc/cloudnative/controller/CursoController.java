package com.duoc.cloudnative.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.cloudnative.dto.CursoRequestDTO;
import com.duoc.cloudnative.entity.Curso;
import com.duoc.cloudnative.service.CursoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/cursos")
public class CursoController {

    private final CursoService cursoService;

    public CursoController(CursoService cursoService) {
        this.cursoService = cursoService;
    }

    // Endpoint para listar todos los cursos disponibles.
    @GetMapping
    public ResponseEntity<List<Curso>> listarCursos() {
        List<Curso> cursos = cursoService.listarCursos();
        return ResponseEntity.ok(cursos);
    }

    // Endpoint para agregar un nuevo curso a la oferta educativa.
    @PostMapping
    public ResponseEntity<Curso> agregarCurso(@Valid @RequestBody CursoRequestDTO cursoRequestDTO) {
        Curso cursoGuardado = cursoService.agregarCurso(cursoRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursoGuardado);
    }

    // Endpoint opcional para buscar cursos por coincidencia en el nombre.
    @GetMapping("/buscar")
    public ResponseEntity<List<Curso>> buscarCursosPorNombre(@RequestParam String nombre) {
        List<Curso> cursos = cursoService.buscarCursosPorNombre(nombre);
        return ResponseEntity.ok(cursos);
    }

    // Endpoint opcional para consultar un curso específico por ID.
    @GetMapping("/{id}")
    public ResponseEntity<Curso> obtenerCursoPorId(@PathVariable Long id) {
        Curso curso = cursoService.obtenerCursoPorId(id);
        return ResponseEntity.ok(curso);
    }
}