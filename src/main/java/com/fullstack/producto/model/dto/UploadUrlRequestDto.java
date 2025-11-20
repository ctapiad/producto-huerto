package com.fullstack.producto.model.dto;

import jakarta.validation.constraints.NotBlank;

public class UploadUrlRequestDto {
    
    @NotBlank(message = "El nombre del archivo es requerido")
    private String fileName;
    
    @NotBlank(message = "El tipo de contenido es requerido")
    private String contentType;

    public UploadUrlRequestDto() {
    }

    public UploadUrlRequestDto(String fileName, String contentType) {
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
