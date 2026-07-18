package com.duoc.cloudnative.dto;

import java.time.LocalDateTime;

public class ColaOperacionResponseDTO {

    private String operacion;
    private String cola;
    private String mensaje;
    private LocalDateTime fecha;
    private Object datos;

    public ColaOperacionResponseDTO() {
    }

    public ColaOperacionResponseDTO(
            String operacion,
            String cola,
            String mensaje,
            LocalDateTime fecha,
            Object datos
    ) {
        this.operacion = operacion;
        this.cola = cola;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.datos = datos;
    }

    public String getOperacion() {
        return operacion;
    }

    public void setOperacion(String operacion) {
        this.operacion = operacion;
    }

    public String getCola() {
        return cola;
    }

    public void setCola(String cola) {
        this.cola = cola;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Object getDatos() {
        return datos;
    }

    public void setDatos(Object datos) {
        this.datos = datos;
    }
}