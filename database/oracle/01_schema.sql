-- ============================================================
-- Proyecto: EFT Inscripción de Cursos
-- Archivo: 01_schema.sql
-- Motor: Oracle Database
-- Esquema de ejecución esperado: EFTCURSOS_APP
--
-- Descripción:
-- Crea las secuencias, tablas, claves primarias,
-- restricciones únicas y relaciones del sistema.
--
-- ADVERTENCIA:
-- Las instrucciones iniciales eliminan los objetos existentes.
-- No ejecutar sobre la base desplegada sin respaldo.
-- ============================================================


-- ============================================================
-- 1. ELIMINACIÓN CONTROLADA DE TABLAS
-- ============================================================

BEGIN
    EXECUTE IMMEDIATE
        'DROP TABLE DETALLE_INSCRIPCIONES CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE
        'DROP TABLE RESUMEN CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE
        'DROP TABLE INSCRIPCIONES CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE
        'DROP TABLE ESTUDIANTES CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE
        'DROP TABLE CURSOS CASCADE CONSTRAINTS PURGE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -942 THEN
            RAISE;
        END IF;
END;
/


-- ============================================================
-- 2. ELIMINACIÓN CONTROLADA DE SECUENCIAS
-- ============================================================

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_CURSO';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_ESTUDIANTE';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_INSCRIPCION';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            RAISE;
        END IF;
END;
/

BEGIN
    EXECUTE IMMEDIATE 'DROP SEQUENCE SEQ_DETALLE_INSCRIPCION';
EXCEPTION
    WHEN OTHERS THEN
        IF SQLCODE != -2289 THEN
            RAISE;
        END IF;
END;
/


-- ============================================================
-- 3. CREACIÓN DE SECUENCIAS
-- ============================================================

CREATE SEQUENCE SEQ_CURSO
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NOMAXVALUE
    CACHE 20
    NOCYCLE;

CREATE SEQUENCE SEQ_ESTUDIANTE
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NOMAXVALUE
    CACHE 20
    NOCYCLE;

CREATE SEQUENCE SEQ_INSCRIPCION
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NOMAXVALUE
    CACHE 20
    NOCYCLE;

CREATE SEQUENCE SEQ_DETALLE_INSCRIPCION
    START WITH 1
    INCREMENT BY 1
    MINVALUE 1
    NOMAXVALUE
    CACHE 20
    NOCYCLE;


-- ============================================================
-- 4. TABLA CURSOS
-- ============================================================

CREATE TABLE CURSOS (
    ID              NUMBER(19, 0)      NOT NULL,
    COSTO           NUMBER(10, 2)      NOT NULL,
    DURACION_HORAS  NUMBER(10, 0)      NOT NULL,
    INSTRUCTOR      VARCHAR2(100 CHAR) NOT NULL,
    NOMBRE          VARCHAR2(100 CHAR) NOT NULL,

    CONSTRAINT PK_CURSOS
        PRIMARY KEY (ID),

    CONSTRAINT CK_CURSOS_DURACION
        CHECK (DURACION_HORAS >= 1)
);


-- ============================================================
-- 5. TABLA ESTUDIANTES
-- ============================================================

CREATE TABLE ESTUDIANTES (
    ID      NUMBER(19, 0)      NOT NULL,
    CORREO  VARCHAR2(150 CHAR) NOT NULL,
    NOMBRE  VARCHAR2(100 CHAR) NOT NULL,

    CONSTRAINT PK_ESTUDIANTES
        PRIMARY KEY (ID),

    CONSTRAINT UK_ESTUDIANTES_CORREO
        UNIQUE (CORREO)
);


-- ============================================================
-- 6. TABLA INSCRIPCIONES
-- ============================================================

CREATE TABLE INSCRIPCIONES (
    ID                 NUMBER(19, 0) NOT NULL,
    FECHA_INSCRIPCION  TIMESTAMP(6)  NOT NULL,
    TOTAL              NUMBER(10, 2) NOT NULL,
    ESTUDIANTE_ID      NUMBER(19, 0) NOT NULL,

    CONSTRAINT PK_INSCRIPCIONES
        PRIMARY KEY (ID),

    CONSTRAINT FK_INSCRIPCIONES_ESTUDIANTE
        FOREIGN KEY (ESTUDIANTE_ID)
        REFERENCES ESTUDIANTES (ID)
);


-- ============================================================
-- 7. TABLA DETALLE_INSCRIPCIONES
-- ============================================================

CREATE TABLE DETALLE_INSCRIPCIONES (
    ID              NUMBER(19, 0) NOT NULL,
    COSTO_CURSO     NUMBER(10, 2) NOT NULL,
    CURSO_ID        NUMBER(19, 0) NOT NULL,
    INSCRIPCION_ID  NUMBER(19, 0) NOT NULL,

    CONSTRAINT PK_DETALLE_INSCRIPCIONES
        PRIMARY KEY (ID),

    CONSTRAINT FK_DETALLE_CURSO
        FOREIGN KEY (CURSO_ID)
        REFERENCES CURSOS (ID),

    CONSTRAINT FK_DETALLE_INSCRIPCION
        FOREIGN KEY (INSCRIPCION_ID)
        REFERENCES INSCRIPCIONES (ID)
);


-- ============================================================
-- 8. TABLA RESUMEN
-- ============================================================

CREATE TABLE RESUMEN (
    ID NUMBER(19, 0)
        GENERATED BY DEFAULT AS IDENTITY
        START WITH 1
        INCREMENT BY 1
        CACHE 20
        NOCYCLE
        NOT NULL,

    CORREO            VARCHAR2(255 CHAR),
    FECHA_INSCRIPCION TIMESTAMP(6),
    ID_INSCRIPCION    NUMBER(19, 0),
    ESTUDIANTE        VARCHAR2(255 CHAR),
    TOTAL             NUMBER(38, 2),

    CONSTRAINT PK_RESUMEN
        PRIMARY KEY (ID)
);


-- ============================================================
-- 9. CONFIRMACIÓN
-- ============================================================

COMMIT;