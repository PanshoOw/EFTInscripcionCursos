package com.duoc.cloudnative.config;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.duoc.cloudnative.entity.Curso;
import com.duoc.cloudnative.repository.CursoRepository;

@Component
public class DataLoader implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);

    private final CursoRepository cursoRepository;

    public DataLoader(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Override
    public void run(String... args) {

        // Evita duplicar cursos si la base de datos ya tiene registros.
        if (cursoRepository.count() == 0) {

            List<Curso> cursosIniciales = List.of(
                    crearCurso(
                            "Java Spring Boot",
                            "Carlos Soto",
                            40,
                            BigDecimal.valueOf(50000)
                    ),
                    crearCurso(
                            "Docker desde cero",
                            "María Torres",
                            24,
                            BigDecimal.valueOf(35000)
                    ),
                    crearCurso(
                            "AWS Cloud Fundamentals",
                            "Felipe Rojas",
                            32,
                            BigDecimal.valueOf(45000)
                    ),
                    crearCurso(
                            "Oracle Cloud y Bases de Datos",
                            "Ana Morales",
                            36,
                            BigDecimal.valueOf(55000)
                    )
            );

            cursoRepository.saveAll(Objects.requireNonNull(cursosIniciales));

            logger.info("Cursos iniciales cargados correctamente: {}", cursosIniciales.size());
        }
    }

    // Método auxiliar para construir objetos Curso de forma ordenada.
    private Curso crearCurso(String nombre, String instructor, Integer duracionHoras, BigDecimal costo) {
        Curso curso = new Curso();
        curso.setNombre(nombre);
        curso.setInstructor(instructor);
        curso.setDuracionHoras(duracionHoras);
        curso.setCosto(costo);
        return curso;
    }
}