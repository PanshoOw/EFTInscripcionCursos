package com.duoc.cloudnative.service;

import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.duoc.cloudnative.config.RabbitMQConfig;
import com.duoc.cloudnative.dto.InscripcionResponseDTO;
import com.duoc.cloudnative.entity.Resumen;
import com.duoc.cloudnative.repository.ResumenRepository;

@Service
public class ConsumidorService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ConsumidorService.class);

    private final RabbitTemplate rabbitTemplate;
    private final ResumenRepository resumenRepository;

    public ConsumidorService(
            RabbitTemplate rabbitTemplate,
            ResumenRepository resumenRepository
    ) {
        this.rabbitTemplate = rabbitTemplate;
        this.resumenRepository = resumenRepository;
    }

    /**
     * Retira manualmente el siguiente mensaje de la cola principal,
     * valida sus datos y almacena el resumen en Oracle.
     *
     * @return resumen persistido o null cuando la cola está vacía.
     */
    @Transactional
    public Resumen consumirSiguienteMensaje() {

        Object mensajeRecibido =
                rabbitTemplate.receiveAndConvert(
                        RabbitMQConfig.COLA_INSCRIPCIONES
                );

        if (mensajeRecibido == null) {

            LOGGER.info(
                    "La cola {} no contiene mensajes pendientes.",
                    RabbitMQConfig.COLA_INSCRIPCIONES
            );

            return null;
        }

        if (!(mensajeRecibido
                instanceof InscripcionResponseDTO mensaje)) {

            String detalle =
                    "El mensaje recibido no corresponde "
                            + "a InscripcionResponseDTO.";

            enviarAColaErrores(
                    mensajeRecibido,
                    detalle
            );

            throw new IllegalStateException(detalle);
        }

        try {
            validarMensaje(mensaje);

            Resumen resumen = construirResumen(mensaje);

            resumenRepository.save(resumen);

            LOGGER.info(
                    "Inscripción {} consumida y almacenada "
                            + "correctamente en Oracle.",
                    mensaje.getIdInscripcion()
            );

            return resumen;

        } catch (Exception ex) {

            String detalleError =
                    obtenerDetalleError(ex);

            enviarAColaErrores(
                    mensaje,
                    detalleError
            );

            LOGGER.error(
                    "No fue posible procesar la inscripción {}.",
                    mensaje.getIdInscripcion(),
                    ex
            );

            throw new IllegalStateException(
                    "El mensaje no pudo ser procesado "
                            + "y fue enviado a la cola de errores.",
                    ex
            );
        }
    }

    /**
     * Construye la entidad que se almacenará en la tabla RESUMEN.
     */
    private Resumen construirResumen(
            InscripcionResponseDTO mensaje
    ) {

        Resumen resumen = new Resumen();

        resumen.setInscripcionId(
                mensaje.getIdInscripcion()
        );

        resumen.setNombreEstudiante(
                mensaje.getEstudiante()
        );

        resumen.setCorreoEstudiante(
                mensaje.getCorreo()
        );

        resumen.setFechaInscripcion(
                mensaje.getFechaInscripcion()
        );

        resumen.setTotal(
                mensaje.getTotal()
        );

        return resumen;
    }

    /**
     * Verifica que el mensaje contenga los datos mínimos
     * requeridos para almacenarlo en Oracle.
     */
    private void validarMensaje(
            InscripcionResponseDTO mensaje
    ) {

        if (mensaje.getIdInscripcion() == null) {
            throw new IllegalArgumentException(
                    "El ID de inscripción es obligatorio."
            );
        }

        if (mensaje.getEstudiante() == null
                || mensaje.getEstudiante().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre del estudiante es obligatorio."
            );
        }

        if (mensaje.getCorreo() == null
                || mensaje.getCorreo().isBlank()) {

            throw new IllegalArgumentException(
                    "El correo del estudiante es obligatorio."
            );
        }

        if (mensaje.getFechaInscripcion() == null) {
            throw new IllegalArgumentException(
                    "La fecha de inscripción es obligatoria."
            );
        }

        if (mensaje.getTotal() == null) {
            throw new IllegalArgumentException(
                    "El total de la inscripción es obligatorio."
            );
        }
    }

    /**
     * Publica en la cola secundaria tanto el mensaje original
     * como la descripción del error producido.
     */
    private void enviarAColaErrores(
            Object mensajeOriginal,
            String detalleError
    ) {

        Map<String, Object> mensajeError =
                new LinkedHashMap<>();

        mensajeError.put(
                "fechaError",
                OffsetDateTime.now().toString()
        );

        mensajeError.put(
                "detalle",
                detalleError
        );

        mensajeError.put(
                "mensajeOriginal",
                mensajeOriginal
        );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COLA_ERRORES,
                mensajeError
        );

        LOGGER.warn(
                "Mensaje enviado a la cola de errores: {}",
                RabbitMQConfig.COLA_ERRORES
        );
    }

    private String obtenerDetalleError(
            Exception ex
    ) {

        if (ex.getMessage() != null
                && !ex.getMessage().isBlank()) {

            return ex.getMessage();
        }

        return ex.getClass().getSimpleName();
    }
}