package com.duoc.cloudnative.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * Cola principal utilizada para almacenar las inscripciones
     * pendientes de procesamiento.
     */
    public static final String COLA_INSCRIPCIONES =
            "inscripciones.procesamiento";

    /**
     * Cola secundaria utilizada para almacenar mensajes
     * que no pudieron procesarse correctamente.
     */
    public static final String COLA_ERRORES =
            "inscripciones.errores";

    /**
     * Alias temporal para mantener compatibilidad con las clases
     * existentes mientras se completa la refactorización.
     */
    public static final String QUEUE =
            COLA_INSCRIPCIONES;

    @Bean
    public Queue colaInscripciones() {
        return new Queue(
                COLA_INSCRIPCIONES,
                true
        );
    }

    @Bean
    public Queue colaErrores() {
        return new Queue(
                COLA_ERRORES,
                true
        );
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(
            ConnectionFactory connectionFactory,
            MessageConverter jsonMessageConverter
    ) {
        RabbitTemplate rabbitTemplate =
                new RabbitTemplate(connectionFactory);

        rabbitTemplate.setMessageConverter(
                jsonMessageConverter
        );

        return rabbitTemplate;
    }
}