package com.duoc.cloudnative.dto;

import java.math.BigDecimal;

public class CursoInscritoDTO {

    private Long idCurso;
    private String nombre;
    private String instructor;
    private BigDecimal costo;

    public CursoInscritoDTO() {
    }

    public CursoInscritoDTO(Long idCurso, String nombre, String instructor, BigDecimal costo) {
        this.idCurso = idCurso;
        this.nombre = nombre;
        this.instructor = instructor;
        this.costo = costo;
    }

    public Long getIdCurso() {
        return idCurso;
    }

    public void setIdCurso(Long idCurso) {
        this.idCurso = idCurso;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
}