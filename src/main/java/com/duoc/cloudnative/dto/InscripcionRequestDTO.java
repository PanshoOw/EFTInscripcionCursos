package com.duoc.cloudnative.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public class InscripcionRequestDTO {

    @NotBlank(message = "El nombre del estudiante es obligatorio")
    private String nombreEstudiante;

    @NotBlank(message = "El correo del estudiante es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    private String correoEstudiante;

    @NotEmpty(message = "Debe seleccionar al menos un curso")
    private List<Long> idsCursos;

    public InscripcionRequestDTO() {
    }

    public InscripcionRequestDTO(String nombreEstudiante, String correoEstudiante, List<Long> idsCursos) {
        this.nombreEstudiante = nombreEstudiante;
        this.correoEstudiante = correoEstudiante;
        this.idsCursos = idsCursos;
    }

    public String getNombreEstudiante() {
        return nombreEstudiante;
    }

    public void setNombreEstudiante(String nombreEstudiante) {
        this.nombreEstudiante = nombreEstudiante;
    }

    public String getCorreoEstudiante() {
        return correoEstudiante;
    }

    public void setCorreoEstudiante(String correoEstudiante) {
        this.correoEstudiante = correoEstudiante;
    }

    public List<Long> getIdsCursos() {
        return idsCursos;
    }

    public void setIdsCursos(List<Long> idsCursos) {
        this.idsCursos = idsCursos;
    }
}