package com.duoc.cloudnative.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.cloudnative.dto.ColaOperacionResponseDTO;
import com.duoc.cloudnative.dto.InscripcionResponseDTO;
import com.duoc.cloudnative.service.BffColasService;

@RestController
@RequestMapping("/api/bff/colas/inscripciones")
public class BffColasController {

    private final BffColasService bffColasService;

    public BffColasController(
            BffColasService bffColasService
    ) {
        this.bffColasService = bffColasService;
    }

    /**
     * Publica una inscripción en la cola principal
     * mediante el componente BFF.
     */
    @PostMapping("/producir")
    public ResponseEntity<ColaOperacionResponseDTO>
    producirMensaje(
            @RequestBody InscripcionResponseDTO mensaje
    ) {

        ColaOperacionResponseDTO respuesta =
                bffColasService.producirInscripcion(
                        mensaje
                );

        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(respuesta);
    }

    /**
     * Consume manualmente el siguiente mensaje
     * disponible en la cola principal.
     */
    @PostMapping("/consumir")
    public ResponseEntity<ColaOperacionResponseDTO>
    consumirMensaje() {

        ColaOperacionResponseDTO respuesta =
                bffColasService.consumirInscripcion();

        return ResponseEntity.ok(respuesta);
    }
}