package com.duoc.cloudnative.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "INSCRIPCIONES")
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "inscripcion_seq")
    @SequenceGenerator(name = "inscripcion_seq", sequenceName = "SEQ_INSCRIPCION", allocationSize = 1)
    private Long id;

    // Muchas inscripciones pueden pertenecer a un mismo estudiante.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "ESTUDIANTE_ID", nullable = false)
    private Estudiante estudiante;

    // Fecha y hora exacta en que se realiza la inscripción.
    @Column(name = "FECHA_INSCRIPCION", nullable = false)
    private LocalDateTime fechaInscripcion;

    // Total a pagar por todos los cursos seleccionados.
    @Column(name = "TOTAL", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    // Una inscripción puede tener varios detalles, uno por cada curso seleccionado.
    @OneToMany(mappedBy = "inscripcion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleInscripcion> detalles = new ArrayList<>();

    public Inscripcion() {
    }

    public Inscripcion(Long id, Estudiante estudiante, LocalDateTime fechaInscripcion, BigDecimal total) {
        this.id = id;
        this.estudiante = estudiante;
        this.fechaInscripcion = fechaInscripcion;
        this.total = total;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public LocalDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public void setFechaInscripcion(LocalDateTime fechaInscripcion) {
        this.fechaInscripcion = fechaInscripcion;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public List<DetalleInscripcion> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleInscripcion> detalles) {
        this.detalles = detalles;
    }
}