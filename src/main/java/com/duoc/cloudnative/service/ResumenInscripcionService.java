package com.duoc.cloudnative.service;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
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
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

@Service
public class ResumenInscripcionService {

    private static final Path DIRECTORIO_RESUMENES =
            Paths.get("resumenes");

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final Locale LOCALE_CHILE =
            new Locale("es", "CL");

    private static final String MENSAJE_ID_INSCRIPCION_NULO =
            "El ID de inscripción no puede ser nulo.";

    private final InscripcionRepository inscripcionRepository;

    public ResumenInscripcionService(
            InscripcionRepository inscripcionRepository
    ) {
        this.inscripcionRepository = inscripcionRepository;
    }

    /**
     * Genera un documento PDF con el resumen completo
     * de una inscripción existente.
     */
    @Transactional(readOnly = true)
    public Path generarArchivoResumen(Long idInscripcion) {

        Long idValidado = Objects.requireNonNull(
                idInscripcion,
                MENSAJE_ID_INSCRIPCION_NULO
        );

        Inscripcion inscripcion = inscripcionRepository
                .findById(idValidado)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe una inscripción con el ID: "
                                + idValidado
                ));

        if (inscripcion.getDetalles() == null
                || inscripcion.getDetalles().isEmpty()) {

            throw new IllegalArgumentException(
                    "La inscripción no tiene cursos asociados "
                            + "para generar el resumen."
            );
        }

        try {
            Files.createDirectories(DIRECTORIO_RESUMENES);

            Path rutaArchivo = DIRECTORIO_RESUMENES.resolve(
                    obtenerNombreArchivo(idValidado)
            );

            crearDocumentoPdf(inscripcion, rutaArchivo);

            return rutaArchivo;

        } catch (IOException | DocumentException ex) {

            throw new IllegalStateException(
                    "No se pudo generar el documento PDF "
                            + "del resumen de inscripción.",
                    ex
            );
        }
    }

    /**
     * Retorna el nombre estándar del documento.
     */
    public String obtenerNombreArchivo(Long idInscripcion) {

        Long idValidado = Objects.requireNonNull(
                idInscripcion,
                MENSAJE_ID_INSCRIPCION_NULO
        );

        return "resumen-inscripcion-"
                + idValidado
                + ".pdf";
    }

    /**
     * Construye la ruta jerárquica utilizada en Amazon S3.
     */
    public String obtenerKeyS3(Long idInscripcion) {

        Long idValidado = Objects.requireNonNull(
                idInscripcion,
                MENSAJE_ID_INSCRIPCION_NULO
        );

        return idValidado
                + "/"
                + obtenerNombreArchivo(idValidado);
    }

    private void crearDocumentoPdf(
            Inscripcion inscripcion,
            Path rutaArchivo
    ) throws IOException, DocumentException {

        Document documento = new Document(
                PageSize.A4,
                45,
                45,
                50,
                50
        );

        try (FileOutputStream salida =
                     new FileOutputStream(rutaArchivo.toFile())) {

            PdfWriter.getInstance(documento, salida);

            documento.open();

            agregarTitulo(documento);
            agregarDatosGenerales(documento, inscripcion);
            agregarDatosEstudiante(documento, inscripcion);
            agregarCursos(documento, inscripcion);
            agregarTotal(documento, inscripcion);

        } finally {

            if (documento.isOpen()) {
                documento.close();
            }
        }
    }

    private void agregarTitulo(Document documento)
            throws DocumentException {

        Font fuenteTitulo = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                18,
                Color.BLACK
        );

        Font fuenteSubtitulo = FontFactory.getFont(
                FontFactory.HELVETICA,
                10,
                Color.DARK_GRAY
        );

        Paragraph titulo = new Paragraph(
                "RESUMEN DE INSCRIPCIÓN",
                fuenteTitulo
        );

        titulo.setAlignment(Element.ALIGN_CENTER);
        titulo.setSpacingAfter(6);

        Paragraph subtitulo = new Paragraph(
                "Plataforma de Gestión de Cursos en Línea",
                fuenteSubtitulo
        );

        subtitulo.setAlignment(Element.ALIGN_CENTER);
        subtitulo.setSpacingAfter(24);

        documento.add(titulo);
        documento.add(subtitulo);
    }

    private void agregarDatosGenerales(
            Document documento,
            Inscripcion inscripcion
    ) throws DocumentException {

        Font fuenteEtiqueta = crearFuenteEtiqueta();
        Font fuenteValor = crearFuenteValor();

        PdfPTable tabla = new PdfPTable(2);

        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{35, 65});
        tabla.setSpacingAfter(18);

        agregarFilaDato(
                tabla,
                "Número de inscripción:",
                String.valueOf(inscripcion.getId()),
                fuenteEtiqueta,
                fuenteValor
        );

        agregarFilaDato(
                tabla,
                "Fecha de inscripción:",
                inscripcion.getFechaInscripcion()
                        .format(FORMATO_FECHA),
                fuenteEtiqueta,
                fuenteValor
        );

        documento.add(tabla);
    }

    private void agregarDatosEstudiante(
            Document documento,
            Inscripcion inscripcion
    ) throws DocumentException {

        documento.add(crearEncabezadoSeccion(
                "DATOS DEL ESTUDIANTE"
        ));

        PdfPTable tabla = new PdfPTable(2);

        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{35, 65});
        tabla.setSpacingAfter(20);

        agregarFilaDato(
                tabla,
                "Nombre:",
                inscripcion.getEstudiante().getNombre(),
                crearFuenteEtiqueta(),
                crearFuenteValor()
        );

        agregarFilaDato(
                tabla,
                "Correo electrónico:",
                inscripcion.getEstudiante().getCorreo(),
                crearFuenteEtiqueta(),
                crearFuenteValor()
        );

        documento.add(tabla);
    }

    private void agregarCursos(
            Document documento,
            Inscripcion inscripcion
    ) throws DocumentException {

        documento.add(crearEncabezadoSeccion(
                "CURSOS INSCRITOS"
        ));

        PdfPTable tabla = new PdfPTable(5);

        tabla.setWidthPercentage(100);
        tabla.setWidths(new float[]{7, 30, 25, 16, 22});
        tabla.setSpacingAfter(18);

        agregarCeldaEncabezado(tabla, "N°");
        agregarCeldaEncabezado(tabla, "Curso");
        agregarCeldaEncabezado(tabla, "Instructor");
        agregarCeldaEncabezado(tabla, "Duración");
        agregarCeldaEncabezado(tabla, "Costo");

        NumberFormat formatoMoneda =
                NumberFormat.getCurrencyInstance(LOCALE_CHILE);

        int numero = 1;

        for (DetalleInscripcion detalle
                : inscripcion.getDetalles()) {

            agregarCeldaContenido(
                    tabla,
                    String.valueOf(numero),
                    Element.ALIGN_CENTER
            );

            agregarCeldaContenido(
                    tabla,
                    detalle.getCurso().getNombre(),
                    Element.ALIGN_LEFT
            );

            agregarCeldaContenido(
                    tabla,
                    detalle.getCurso().getInstructor(),
                    Element.ALIGN_LEFT
            );

            agregarCeldaContenido(
                    tabla,
                    detalle.getCurso().getDuracionHoras()
                            + " horas",
                    Element.ALIGN_CENTER
            );

            agregarCeldaContenido(
                    tabla,
                    formatearMoneda(
                            detalle.getCostoCurso(),
                            formatoMoneda
                    ),
                    Element.ALIGN_RIGHT
            );

            numero++;
        }

        documento.add(tabla);
    }

    private void agregarTotal(
            Document documento,
            Inscripcion inscripcion
    ) throws DocumentException {

        NumberFormat formatoMoneda =
                NumberFormat.getCurrencyInstance(LOCALE_CHILE);

        Font fuenteTotal = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                13,
                Color.BLACK
        );

        Paragraph total = new Paragraph(
                "TOTAL A PAGAR: "
                        + formatearMoneda(
                                inscripcion.getTotal(),
                                formatoMoneda
                        ),
                fuenteTotal
        );

        total.setAlignment(Element.ALIGN_RIGHT);
        total.setSpacingBefore(8);

        documento.add(total);
    }

    private Paragraph crearEncabezadoSeccion(String texto) {

        Font fuente = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                12,
                Color.BLACK
        );

        Paragraph encabezado = new Paragraph(texto, fuente);

        encabezado.setSpacingBefore(6);
        encabezado.setSpacingAfter(8);

        return encabezado;
    }

    private void agregarFilaDato(
            PdfPTable tabla,
            String etiqueta,
            String valor,
            Font fuenteEtiqueta,
            Font fuenteValor
    ) {

        PdfPCell celdaEtiqueta = new PdfPCell(
                new Phrase(etiqueta, fuenteEtiqueta)
        );

        celdaEtiqueta.setBorder(PdfPCell.NO_BORDER);
        celdaEtiqueta.setPadding(5);

        PdfPCell celdaValor = new PdfPCell(
                new Phrase(valor, fuenteValor)
        );

        celdaValor.setBorder(PdfPCell.NO_BORDER);
        celdaValor.setPadding(5);

        tabla.addCell(celdaEtiqueta);
        tabla.addCell(celdaValor);
    }

    private void agregarCeldaEncabezado(
            PdfPTable tabla,
            String texto
    ) {

        Font fuente = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                9,
                Color.WHITE
        );

        PdfPCell celda = new PdfPCell(
                new Phrase(texto, fuente)
        );

        celda.setBackgroundColor(new Color(55, 71, 79));
        celda.setHorizontalAlignment(Element.ALIGN_CENTER);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setPadding(7);

        tabla.addCell(celda);
    }

    private void agregarCeldaContenido(
            PdfPTable tabla,
            String texto,
            int alineacion
    ) {

        Font fuente = FontFactory.getFont(
                FontFactory.HELVETICA,
                9,
                Color.BLACK
        );

        PdfPCell celda = new PdfPCell(
                new Phrase(texto, fuente)
        );

        celda.setHorizontalAlignment(alineacion);
        celda.setVerticalAlignment(Element.ALIGN_MIDDLE);
        celda.setPadding(6);

        tabla.addCell(celda);
    }

    private Font crearFuenteEtiqueta() {

        return FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                10,
                Color.BLACK
        );
    }

    private Font crearFuenteValor() {

        return FontFactory.getFont(
                FontFactory.HELVETICA,
                10,
                Color.BLACK
        );
    }

    private String formatearMoneda(
            BigDecimal valor,
            NumberFormat formatoMoneda
    ) {

        BigDecimal valorSeguro =
                valor != null ? valor : BigDecimal.ZERO;

        return formatoMoneda.format(valorSeguro);
    }
}