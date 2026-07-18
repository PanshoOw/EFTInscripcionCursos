-- ============================================================
-- Proyecto: EFT Inscripción de Cursos
-- Archivo: 02_data.sql
-- Motor: Oracle Database
-- Esquema de ejecución esperado: EFTCURSOS_APP
--
-- Descripción:
-- Registra los cursos iniciales requeridos para realizar
-- pruebas de consulta e inscripción.
--
-- Requisito previo:
-- Ejecutar primero el archivo 01_schema.sql.
-- ============================================================


-- ============================================================
-- 1. CURSOS INICIALES
-- ============================================================

INSERT INTO CURSOS (
    ID,
    COSTO,
    DURACION_HORAS,
    INSTRUCTOR,
    NOMBRE
) VALUES (
    SEQ_CURSO.NEXTVAL,
    50000.00,
    40,
    'Carlos Soto',
    'Java Spring Boot'
);

INSERT INTO CURSOS (
    ID,
    COSTO,
    DURACION_HORAS,
    INSTRUCTOR,
    NOMBRE
) VALUES (
    SEQ_CURSO.NEXTVAL,
    35000.00,
    24,
    'María Torres',
    'Docker desde cero'
);

INSERT INTO CURSOS (
    ID,
    COSTO,
    DURACION_HORAS,
    INSTRUCTOR,
    NOMBRE
) VALUES (
    SEQ_CURSO.NEXTVAL,
    45000.00,
    32,
    'Felipe Rojas',
    'AWS Cloud Fundamentals'
);

INSERT INTO CURSOS (
    ID,
    COSTO,
    DURACION_HORAS,
    INSTRUCTOR,
    NOMBRE
) VALUES (
    SEQ_CURSO.NEXTVAL,
    55000.00,
    36,
    'Ana Morales',
    'Oracle Cloud y Base de Datos'
);


-- ============================================================
-- 2. CONFIRMACIÓN DE TRANSACCIÓN
-- ============================================================

COMMIT;


-- ============================================================
-- 3. VERIFICACIÓN
-- ============================================================

SELECT
    ID,
    NOMBRE,
    INSTRUCTOR,
    DURACION_HORAS,
    COSTO
FROM CURSOS
ORDER BY ID;