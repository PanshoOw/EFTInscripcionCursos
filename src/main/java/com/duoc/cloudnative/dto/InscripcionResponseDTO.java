package com.duoc.cloudnative.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class InscripcionResponseDTO {

    private Long idInscripcion;
    private String estudiante;
    private String correo;
    private LocalDateTime fechaInscripcion;
    private List<CursoInscritoDTO> cursos;
    private BigDecimal total;

    public InscripcionResponseDTO() {
    }

    public InscripcionResponseDTO(Long idInscripcion, String estudiante, String correo,
                                  LocalDateTime fechaInscripcion, List<CursoInscritoDTO> cursos,
                                  BigDecimal total) {
        this.idInscripcion = idInscripcion;
        this.estudiante = estudiante;
        this.correo = correo;
        this.fechaInscripcion = fechaInscripcion;
        this.cursos = cursos;
        this.total = total;
    }

    public Long getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(Long idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public String getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(String estudiante) {
        this.estudiante = estudiante;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public List<CursoInscritoDTO> getCursos() {
        return cursos;
    }

    public void setCursos(List<CursoInscritoDTO> cursos) {
        this.cursos = cursos;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}