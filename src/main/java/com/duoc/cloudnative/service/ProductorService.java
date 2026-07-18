package com.duoc.cloudnative.service;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.duoc.cloudnative.config.RabbitMQConfig;
import com.duoc.cloudnative.dto.InscripcionResponseDTO;

@Service
public class ProductorService {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ProductorService.class);

    private static final String MENSAJE_NULO =
            "El mensaje que se enviará a RabbitMQ no puede ser nulo.";

    private final RabbitTemplate rabbitTemplate;

    public ProductorService(
            RabbitTemplate rabbitTemplate
    ) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Publica una inscripción en la cola principal
     * de procesamiento de RabbitMQ.
     */
    public void enviarMensaje(
            InscripcionResponseDTO mensaje
    ) {

        InscripcionResponseDTO mensajeValidado =
                Objects.requireNonNull(
                        mensaje,
                        MENSAJE_NULO
                );

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.COLA_INSCRIPCIONES,
                mensajeValidado
        );

        LOGGER.info(
                "Inscripción {} enviada correctamente a la cola {}.",
                mensajeValidado.getIdInscripcion(),
                RabbitMQConfig.COLA_INSCRIPCIONES
        );
    }
}