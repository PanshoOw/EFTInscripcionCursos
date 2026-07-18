# Scripts de base de datos Oracle

Esta carpeta contiene los scripts necesarios para reconstruir la base de datos Oracle utilizada por el proyecto **EFT Inscripción de Cursos**.

## Archivos

### `01_schema.sql`

Crea la estructura completa de la base de datos:

- Secuencias para la generación de identificadores.
- Tabla `CURSOS`.
- Tabla `ESTUDIANTES`.
- Tabla `INSCRIPCIONES`.
- Tabla `DETALLE_INSCRIPCIONES`.
- Tabla `RESUMEN`.
- Claves primarias.
- Claves foráneas.
- Restricción única para el correo del estudiante.
- Restricciones de validación.

El script elimina previamente los objetos existentes, por lo que debe ejecutarse únicamente cuando se quiera reconstruir el esquema desde cero.

### `02_data.sql`

Inserta los cursos iniciales utilizados para probar las operaciones de consulta e inscripción.

Debe ejecutarse después de `01_schema.sql`.

## Orden de ejecución

```text
1. 01_schema.sql
2. 02_data.sql