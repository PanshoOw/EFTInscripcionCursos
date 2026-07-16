package com.duoc.cloudnative.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "DETALLE_INSCRIPCIONES")
public class DetalleInscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "detalle_inscripcion_seq")
    @SequenceGenerator(name = "detalle_inscripcion_seq", sequenceName = "SEQ_DETALLE_INSCRIPCION", allocationSize = 1)
    private Long id;

    // Muchos detalles pueden pertenecer a una misma inscripción.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "INSCRIPCION_ID", nullable = false)
    private Inscripcion inscripcion;

    // Muchos detalles pueden hacer referencia a un mismo curso.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "CURSO_ID", nullable = false)
    private Curso curso;

    // Guarda el costo del curso al momento exacto de la inscripción.
    @Column(name = "COSTO_CURSO", nullable = false, precision = 10, scale = 2)
    private BigDecimal costoCurso;

    public DetalleInscripcion() {
    }

    public DetalleInscripcion(Long id, Inscripcion inscripcion, Curso curso, BigDecimal costoCurso) {
        this.id = id;
        this.inscripcion = inscripcion;
        this.curso = curso;
        this.costoCurso = costoCurso;
    }

    public Long getId() {
        return id;
    }

    public Inscripcion getInscripcion() {
        return inscripcion;
    }

    public Curso getCurso() {
        return curso;
    }

    public BigDecimal getCostoCurso() {
        return costoCurso;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setInscripcion(Inscripcion inscripcion) {
        this.inscripcion = inscripcion;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

    public void setCostoCurso(BigDecimal costoCurso) {
        this.costoCurso = costoCurso;
    }
}