package com.duoc.cloudnative.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.cloudnative.dto.InscripcionRequestDTO;
import com.duoc.cloudnative.dto.InscripcionResponseDTO;
import com.duoc.cloudnative.service.InscripcionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inscripciones")
public class InscripcionController {

    private final InscripcionService inscripcionService;

    public InscripcionController(InscripcionService inscripcionService) {
        this.inscripcionService = inscripcionService;
    }

    // Endpoint para inscribir a un estudiante en uno o más cursos.
    @PostMapping
    public ResponseEntity<InscripcionResponseDTO> registrarInscripcion(
            @Valid @RequestBody InscripcionRequestDTO requestDTO) {

        InscripcionResponseDTO respuesta = inscripcionService.registrarInscripcion(requestDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }
}