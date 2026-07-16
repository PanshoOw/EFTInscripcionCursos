package com.duoc.cloudnative.controller;

import java.nio.file.Path;
import java.util.Objects;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.cloudnative.dto.ResumenS3ResponseDTO;
import com.duoc.cloudnative.service.ResumenInscripcionService;
import com.duoc.cloudnative.service.ResumenS3Service;

@RestController
@RequestMapping("/api/inscripciones")
public class ResumenInscripcionController {

    private static final String MENSAJE_RUTA_ARCHIVO_NULA = "La ruta del archivo generado no puede ser nula.";
    private static final String TIPO_CONTENIDO_TEXTO_PLANO = "text/plain; charset=UTF-8";

    private final ResumenInscripcionService resumenInscripcionService;
    private final ResumenS3Service resumenS3Service;

    public ResumenInscripcionController(ResumenInscripcionService resumenInscripcionService,
                                        ResumenS3Service resumenS3Service) {
        this.resumenInscripcionService = resumenInscripcionService;
        this.resumenS3Service = resumenS3Service;
    }

    // Genera un archivo físico del resumen y lo entrega como descarga.
    @GetMapping("/{id}/resumen/archivo")
    public ResponseEntity<Resource> generarArchivoResumen(@PathVariable Long id) {

        Path rutaArchivo = Objects.requireNonNull(
                resumenInscripcionService.generarArchivoResumen(id),
                MENSAJE_RUTA_ARCHIVO_NULA
        );

        Resource recurso = new FileSystemResource(rutaArchivo);

        String nombreArchivo = resumenInscripcionService.obtenerNombreArchivo(id);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(nombreArchivo)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, TIPO_CONTENIDO_TEXTO_PLANO)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(recurso);
    }

    // Genera el resumen y lo sube al bucket S3 dentro de una carpeta con el ID de inscripción.
    @PostMapping("/{id}/resumen/s3")
    public ResponseEntity<ResumenS3ResponseDTO> subirResumenAS3(@PathVariable Long id) {

        ResumenS3ResponseDTO respuesta = resumenS3Service.subirResumenAS3(id);

        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    // Reemplaza el resumen existente en S3, manteniendo la carpeta correspondiente al ID de inscripción.
    @PutMapping("/{id}/resumen/s3")
    public ResponseEntity<ResumenS3ResponseDTO> reemplazarResumenEnS3(@PathVariable Long id) {

        ResumenS3ResponseDTO respuesta = resumenS3Service.reemplazarResumenEnS3(id);

        return ResponseEntity.ok(respuesta);
    }

    // Descarga desde S3 el resumen correspondiente a la inscripción indicada.
    @GetMapping("/{id}/resumen/s3")
    public ResponseEntity<byte[]> descargarResumenDesdeS3(@PathVariable Long id) {

        byte[] archivo = resumenS3Service.descargarResumenDesdeS3(id);

        String nombreArchivo = resumenS3Service.obtenerNombreArchivo(id);

        ContentDisposition contentDisposition = ContentDisposition.attachment()
                .filename(nombreArchivo)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, TIPO_CONTENIDO_TEXTO_PLANO)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition.toString())
                .body(archivo);
    }

    // Elimina desde S3 el resumen correspondiente a la inscripción indicada.
    @DeleteMapping("/{id}/resumen/s3")
    public ResponseEntity<ResumenS3ResponseDTO> eliminarResumenDesdeS3(@PathVariable Long id) {

        ResumenS3ResponseDTO respuesta = resumenS3Service.eliminarResumenDesdeS3(id);

        return ResponseEntity.ok(respuesta);
    }
}