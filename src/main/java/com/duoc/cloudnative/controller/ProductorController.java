package com.duoc.cloudnative.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.duoc.cloudnative.dto.InscripcionResponseDTO;
import com.duoc.cloudnative.service.ProductorService;

@RestController
@RequestMapping("/rabbit")
public class ProductorController {

    private final ProductorService producer;

    public ProductorController(ProductorService producer) {
        this.producer = producer;
    }

    @PostMapping("/enviar")
    public String enviarMensaje(@RequestBody InscripcionResponseDTO mensaje) {
        producer.enviarMensaje(mensaje);
        return "Mensaje enviado correctamente a RabbitMQ";
    }
}

