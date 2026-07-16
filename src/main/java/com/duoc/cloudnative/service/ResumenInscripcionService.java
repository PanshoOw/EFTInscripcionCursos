package com.duoc.cloudnative.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.cloudnative.entity.DetalleInscripcion;
import com.duoc.cloudnative.entity.Inscripcion;
import com.duoc.cloudnative.repository.InscripcionRepository;

@Service
public class ResumenInscripcionService {

    private static final Path DIRECTORIO_RESUMENES = Paths.get("resumenes");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    private static final Locale LOCALE_CHILE = new Locale("es", "CL");

    private static final String MENSAJE_ID_INSCRIPCION_NULO = "El ID de inscripción no puede ser nulo.";
    private static final String SEPARADOR_SIMPLE = "----------------------------------------\n";
    private static final String SEPARADOR_DOBLE = "========================================\n";

    private final InscripcionRepository inscripcionRepository;

    public ResumenInscripcionService(InscripcionRepository inscripcionRepository) {
        this.inscripcionRepository = inscripcionRepository;
    }

    // Genera un archivo físico .txt con el resumen completo de una inscripción existente.
    @Transactional(readOnly = true)
    public Path generarArchivoResumen(Long idInscripcion) {

        Long idValidado = Objects.requireNonNull(idInscripcion, MENSAJE_ID_INSCRIPCION_NULO);

        Inscripcion inscripcion = inscripcionRepository.findById(idValidado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una inscripción con el ID: " + idValidado
                ));

        if (inscripcion.getDetalles() == null || inscripcion.getDetalles().isEmpty()) {
            throw new IllegalArgumentException(
                    "La inscripción no tiene cursos asociados para generar el resumen."
            );
        }

        String contenido = construirContenidoResumen(inscripcion);
        String nombreArchivo = obtenerNombreArchivo(idValidado);

        try {
            Files.createDirectories(DIRECTORIO_RESUMENES);

            Path rutaArchivo = DIRECTORIO_RESUMENES.resolve(nombreArchivo);

            Files.writeString(
                    rutaArchivo,
                    contenido,
                    StandardCharsets.UTF_8
            );

            return rutaArchivo;

        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo generar el archivo físico del resumen.", ex);
        }
    }

    // Construye el nombre estándar del archivo resumen.
    public String obtenerNombreArchivo(Long idInscripcion) {
        Long idValidado = Objects.requireNonNull(idInscripcion, MENSAJE_ID_INSCRIPCION_NULO);
        return "resumen-inscripcion-" + idValidado + ".txt";
    }

    // Construye la ruta jerárquica que se usará posteriormente en S3.
    public String obtenerKeyS3(Long idInscripcion) {
        Long idValidado = Objects.requireNonNull(idInscripcion, MENSAJE_ID_INSCRIPCION_NULO);
        return idValidado + "/" + obtenerNombreArchivo(idValidado);
    }

    private String construirContenidoResumen(Inscripcion inscripcion) {

        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(LOCALE_CHILE);
        StringBuilder resumen = new StringBuilder();

        resumen.append(SEPARADOR_DOBLE);
        resumen.append("        RESUMEN DE INSCRIPCIÓN\n");
        resumen.append(SEPARADOR_DOBLE).append("\n");

        resumen.append("Número de resumen: ")
                .append(inscripcion.getId())
                .append("\n");

        resumen.append("Fecha de inscripción: ")
                .append(inscripcion.getFechaInscripcion().format(FORMATO_FECHA))
                .append("\n\n");

        resumen.append("DATOS DEL ESTUDIANTE\n");
        resumen.append(SEPARADOR_SIMPLE);
        resumen.append("Nombre: ")
                .append(inscripcion.getEstudiante().getNombre())
                .append("\n");

        resumen.append("Correo: ")
                .append(inscripcion.getEstudiante().getCorreo())
                .append("\n\n");

        resumen.append("CURSOS INSCRITOS\n");
        resumen.append(SEPARADOR_SIMPLE);

        int contador = 1;

        for (DetalleInscripcion detalle : inscripcion.getDetalles()) {
            resumen.append(contador)
                    .append(". ")
                    .append(detalle.getCurso().getNombre())
                    .append("\n");

            resumen.append("   Instructor: ")
                    .append(detalle.getCurso().getInstructor())
                    .append("\n");

            resumen.append("   Duración: ")
                    .append(detalle.getCurso().getDuracionHoras())
                    .append(" horas\n");

            resumen.append("   Costo: ")
                    .append(formatearMoneda(detalle.getCostoCurso(), formatoMoneda))
                    .append("\n\n");

            contador++;
        }

        resumen.append(SEPARADOR_SIMPLE);
        resumen.append("TOTAL A PAGAR: ")
                .append(formatearMoneda(inscripcion.getTotal(), formatoMoneda))
                .append("\n");
        resumen.append(SEPARADOR_SIMPLE);

        return resumen.toString();
    }

    private String formatearMoneda(BigDecimal valor, NumberFormat formatoMoneda) {
        BigDecimal valorSeguro = valor != null ? valor : BigDecimal.ZERO;
        return formatoMoneda.format(valorSeguro);
    }
}