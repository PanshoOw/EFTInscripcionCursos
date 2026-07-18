package com.duoc.cloudnative.service;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.duoc.cloudnative.dto.ResumenS3ResponseDTO;

@Service
public class ResumenS3Service {

    private static final String MENSAJE_ID_INSCRIPCION_NULO =
            "El ID de inscripción no puede ser nulo.";

    private static final String MENSAJE_RUTA_ARCHIVO_NULA =
            "La ruta del archivo generado no puede ser nula.";

    private static final String TIPO_CONTENIDO_PDF =
            "application/pdf";

    private static final String MENSAJE_ARCHIVO_SUBIDO =
            "Resumen PDF de inscripción subido correctamente a AWS S3.";

    private static final String MENSAJE_ARCHIVO_REEMPLAZADO =
            "Resumen PDF de inscripción reemplazado correctamente en AWS S3.";

    private static final String MENSAJE_ARCHIVO_ELIMINADO =
            "Resumen PDF de inscripción eliminado correctamente desde AWS S3.";

    private final AmazonS3 s3Client;
    private final ResumenInscripcionService resumenInscripcionService;
    private final String bucketName;

    public ResumenS3Service(
            AmazonS3 s3Client,
            ResumenInscripcionService resumenInscripcionService,
            @Value("${app.s3.bucket-name}") String bucketName
    ) {
        this.s3Client = s3Client;
        this.resumenInscripcionService =
                resumenInscripcionService;
        this.bucketName = bucketName;
    }

    public ResumenS3ResponseDTO subirResumenAS3(
            Long idInscripcion
    ) {

        Long idValidado =
                validarIdInscripcion(idInscripcion);

        validarBucketConfigurado();

        Path rutaArchivo =
                generarArchivoResumenValidado(idValidado);

        String keyS3 =
                resumenInscripcionService.obtenerKeyS3(
                        idValidado
                );

        String nombreArchivo =
                resumenInscripcionService.obtenerNombreArchivo(
                        idValidado
                );

        subirArchivo(rutaArchivo, keyS3);

        return new ResumenS3ResponseDTO(
                idValidado,
                bucketName,
                keyS3,
                nombreArchivo,
                MENSAJE_ARCHIVO_SUBIDO
        );
    }

    public ResumenS3ResponseDTO reemplazarResumenEnS3(
            Long idInscripcion
    ) {

        Long idValidado =
                validarIdInscripcion(idInscripcion);

        validarBucketConfigurado();

        String keyS3 =
                resumenInscripcionService.obtenerKeyS3(
                        idValidado
                );

        String nombreArchivo =
                resumenInscripcionService.obtenerNombreArchivo(
                        idValidado
                );

        validarExistenciaArchivoS3(keyS3);

        Path rutaArchivo =
                generarArchivoResumenValidado(idValidado);

        subirArchivo(rutaArchivo, keyS3);

        return new ResumenS3ResponseDTO(
                idValidado,
                bucketName,
                keyS3,
                nombreArchivo,
                MENSAJE_ARCHIVO_REEMPLAZADO
        );
    }

    public byte[] descargarResumenDesdeS3(
            Long idInscripcion
    ) {

        Long idValidado =
                validarIdInscripcion(idInscripcion);

        validarBucketConfigurado();

        String keyS3 =
                resumenInscripcionService.obtenerKeyS3(
                        idValidado
                );

        validarExistenciaArchivoS3(keyS3);

        try (S3Object objetoS3 =
                     s3Client.getObject(bucketName, keyS3);

             S3ObjectInputStream inputStream =
                     objetoS3.getObjectContent()) {

            return inputStream.readAllBytes();

        } catch (IOException ex) {

            throw new IllegalStateException(
                    "No se pudo descargar el resumen PDF "
                            + "desde AWS S3.",
                    ex
            );
        }
    }

    public ResumenS3ResponseDTO eliminarResumenDesdeS3(
            Long idInscripcion
    ) {

        Long idValidado =
                validarIdInscripcion(idInscripcion);

        validarBucketConfigurado();

        String keyS3 =
                resumenInscripcionService.obtenerKeyS3(
                        idValidado
                );

        String nombreArchivo =
                resumenInscripcionService.obtenerNombreArchivo(
                        idValidado
                );

        validarExistenciaArchivoS3(keyS3);

        DeleteObjectRequest deleteObjectRequest =
                new DeleteObjectRequest(
                        bucketName,
                        keyS3
                );

        s3Client.deleteObject(deleteObjectRequest);

        return new ResumenS3ResponseDTO(
                idValidado,
                bucketName,
                keyS3,
                nombreArchivo,
                MENSAJE_ARCHIVO_ELIMINADO
        );
    }

    public String obtenerNombreArchivo(
            Long idInscripcion
    ) {

        Long idValidado =
                validarIdInscripcion(idInscripcion);

        return resumenInscripcionService
                .obtenerNombreArchivo(idValidado);
    }

    private Long validarIdInscripcion(
            Long idInscripcion
    ) {

        return Objects.requireNonNull(
                idInscripcion,
                MENSAJE_ID_INSCRIPCION_NULO
        );
    }

    private void validarBucketConfigurado() {

        if (!StringUtils.hasText(bucketName)) {

            throw new IllegalStateException(
                    "El nombre del bucket S3 no está configurado. "
                            + "Revise la variable AWS_S3_BUCKET_NAME."
            );
        }
    }

    private Path generarArchivoResumenValidado(
            Long idInscripcion
    ) {

        return Objects.requireNonNull(
                resumenInscripcionService
                        .generarArchivoResumen(idInscripcion),
                MENSAJE_RUTA_ARCHIVO_NULA
        );
    }

    private void validarExistenciaArchivoS3(
            String keyS3
    ) {

        if (!s3Client.doesObjectExist(
                bucketName,
                keyS3
        )) {

            throw new IllegalArgumentException(
                    "No existe un resumen PDF almacenado "
                            + "en S3 con la ruta: "
                            + keyS3
            );
        }
    }

    private void subirArchivo(
            Path rutaArchivo,
            String keyS3
    ) {

        ObjectMetadata metadata =
                new ObjectMetadata();

        metadata.setContentType(TIPO_CONTENIDO_PDF);
        metadata.setContentDisposition(
                "attachment; filename=\""
                        + rutaArchivo.getFileName()
                        + "\""
        );

        PutObjectRequest putObjectRequest =
                new PutObjectRequest(
                        bucketName,
                        keyS3,
                        rutaArchivo.toFile()
                ).withMetadata(metadata);

        s3Client.putObject(putObjectRequest);
    }
}