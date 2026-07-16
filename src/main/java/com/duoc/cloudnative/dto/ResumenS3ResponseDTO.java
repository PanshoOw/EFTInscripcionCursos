package com.duoc.cloudnative.dto;

public class ResumenS3ResponseDTO {

    private Long idInscripcion;
    private String bucket;
    private String keyS3;
    private String nombreArchivo;
    private String mensaje;

    public ResumenS3ResponseDTO() {
    }

    public ResumenS3ResponseDTO(Long idInscripcion, String bucket, String keyS3, String nombreArchivo, String mensaje) {
        this.idInscripcion = idInscripcion;
        this.bucket = bucket;
        this.keyS3 = keyS3;
        this.nombreArchivo = nombreArchivo;
        this.mensaje = mensaje;
    }

    public Long getIdInscripcion() {
        return idInscripcion;
    }

    public void setIdInscripcion(Long idInscripcion) {
        this.idInscripcion = idInscripcion;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKeyS3() {
        return keyS3;
    }

    public void setKeyS3(String keyS3) {
        this.keyS3 = keyS3;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}