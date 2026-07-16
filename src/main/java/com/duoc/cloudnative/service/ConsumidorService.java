package com.duoc.cloudnative.service;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import com.duoc.cloudnative.config.RabbitMQConfig;
import com.duoc.cloudnative.dto.InscripcionResponseDTO;
import com.duoc.cloudnative.entity.Resumen;
import com.duoc.cloudnative.repository.ResumenRepository;

@Service
public class ConsumidorService {

    private final ResumenRepository resumenRepository;

    public ConsumidorService(ResumenRepository resumenRepository) {
        this.resumenRepository = resumenRepository;
    }

    @RabbitListener(queues = RabbitMQConfig.QUEUE)
    public void recibirMensaje(InscripcionResponseDTO mensaje) {

        System.out.println("Mensaje recibido");

        Resumen resumen = new Resumen();

        resumen.setInscripcionId(mensaje.getIdInscripcion());
        resumen.setNombreEstudiante(mensaje.getEstudiante());
        resumen.setCorreoEstudiante(mensaje.getCorreo());
        resumen.setFechaInscripcion(mensaje.getFechaInscripcion());
        resumen.setTotal(mensaje.getTotal());

        resumenRepository.save(resumen);

        System.out.println("Guardado en Oracle");
    }
}
