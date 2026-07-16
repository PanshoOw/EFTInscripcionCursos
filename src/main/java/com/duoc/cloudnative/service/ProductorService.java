package com.duoc.cloudnative.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.duoc.cloudnative.config.RabbitMQConfig;
import com.duoc.cloudnative.dto.InscripcionResponseDTO;

@Service
public class ProductorService {

    private final RabbitTemplate rabbitTemplate;

    public ProductorService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

public void enviarMensaje(InscripcionResponseDTO mensaje) {

    System.out.println("Enviando a RabbitMQ: " + mensaje);

    rabbitTemplate.convertAndSend(
            RabbitMQConfig.QUEUE,
            mensaje
    );
}
}

