package com.duoc.cloudnative.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "CURSOS")
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "curso_seq")
    @SequenceGenerator(name = "curso_seq", sequenceName = "SEQ_CURSO", allocationSize = 1)
    private Long id;

    @NotBlank(message = "El nombre del curso es obligatorio")
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El instructor del curso es obligatorio")
    @Column(name = "INSTRUCTOR", nullable = false, length = 100)
    private String instructor;

    @NotNull(message = "La duración del curso es obligatoria")
    @Min(value = 1, message = "La duración debe ser mayor a 0 horas")
    @Column(name = "DURACION_HORAS", nullable = false)
    private Integer duracionHoras;

    @NotNull(message = "El costo del curso es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El costo debe ser mayor a 0")
    @Column(name = "COSTO", nullable = false, precision = 10, scale = 2)
    private BigDecimal costo;

    public Curso() {
    }

    public Curso(Long id, String nombre, String instructor, Integer duracionHoras, BigDecimal costo) {
        this.id = id;
        this.nombre = nombre;
        this.instructor = instructor;
        this.duracionHoras = duracionHoras;
        this.costo = costo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getDuracionHoras() {
        return duracionHoras;
    }

    public void setDuracionHoras(Integer duracionHoras) {
        this.duracionHoras = duracionHoras;
    }

    public BigDecimal getCosto() {
        return costo;
    }

    public void setCosto(BigDecimal costo) {
        this.costo = costo;
    }
}