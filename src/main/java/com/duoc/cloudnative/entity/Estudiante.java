package com.duoc.cloudnative.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "ESTUDIANTES")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "estudiante_seq")
    @SequenceGenerator(name = "estudiante_seq", sequenceName = "SEQ_ESTUDIANTE", allocationSize = 1)
    private Long id;

    @NotBlank(message = "El nombre del estudiante es obligatorio")
    @Column(name = "NOMBRE", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El correo del estudiante es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Column(name = "CORREO", nullable = false, unique = true, length = 150)
    private String correo;

    public Estudiante() {
    }

    public Estudiante(Long id, String nombre, String correo) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }
}