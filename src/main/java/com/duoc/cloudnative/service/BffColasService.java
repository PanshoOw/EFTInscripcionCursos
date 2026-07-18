package com.duoc.cloudnative.service;

import java.time.LocalDateTime;
import java.util.Objects;

import org.springframework.stereotype.Service;

import com.duoc.cloudnative.config.RabbitMQConfig;
import com.duoc.cloudnative.dto.ColaOperacionResponseDTO;
import com.duoc.cloudnative.dto.InscripcionResponseDTO;
import com.duoc.cloudnative.entity.Resumen;

@Service
public class BffColasService {

    private static final String OPERACION_PRODUCIR =
            "PRODUCIR";

    private static final String OPERACION_CONSUMIR =
            "CONSUMIR";

    private final ProductorService productorService;
    private final ConsumidorService consumidorService;

    public BffColasService(
            ProductorService productorService,
            ConsumidorService consumidorService
    ) {
        this.productorService = productorService;
        this.consumidorService = consumidorService;
    }

    /**
     * Orquesta la publicación de una inscripción
     * en la cola principal de RabbitMQ.
     */
    public ColaOperacionResponseDTO producirInscripcion(
            InscripcionResponseDTO mensaje
    ) {

        InscripcionResponseDTO mensajeValidado =
                Objects.requireNonNull(
                        mensaje,
                        "El mensaje de inscripción no puede ser nulo."
                );

        productorService.enviarMensaje(mensajeValidado);

        return new ColaOperacionResponseDTO(
                OPERACION_PRODUCIR,
                RabbitMQConfig.COLA_INSCRIPCIONES,
                "Mensaje publicado correctamente "
                        + "en la cola de procesamiento.",
                LocalDateTime.now(),
                mensajeValidado
        );
    }

    /**
     * Orquesta el consumo manual del siguiente mensaje
     * disponible en la cola principal.
     */
    public ColaOperacionResponseDTO consumirInscripcion() {

        Resumen resumen =
                consumidorService.consumirSiguienteMensaje();

        if (resumen == null) {

            return new ColaOperacionResponseDTO(
                    OPERACION_CONSUMIR,
                    RabbitMQConfig.COLA_INSCRIPCIONES,
                    "La cola no contiene mensajes pendientes.",
                    LocalDateTime.now(),
                    null
            );
        }

        return new ColaOperacionResponseDTO(
                OPERACION_CONSUMIR,
                RabbitMQConfig.COLA_INSCRIPCIONES,
                "Mensaje consumido y almacenado "
                        + "correctamente en Oracle.",
                LocalDateTime.now(),
                resumen
        );
    }
}