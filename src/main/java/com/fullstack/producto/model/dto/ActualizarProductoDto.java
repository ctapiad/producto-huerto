package com.fullstack.producto.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarProductoDto {
    
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
    
    @Size(max = 255, message = "El link de imagen no puede tener más de 255 caracteres")
    @Pattern(regexp = "^(https?://).*", message = "El link de imagen debe ser una URL válida", groups = {})
    private String linkImagen;
    
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;
    
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Integer precio;
    
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    @Size(max = 100, message = "El origen no puede tener más de 100 caracteres")
    private String origen;
    
    private Boolean certificacionOrganica;
    
    private Boolean estaActivo;
    
    private Integer idCategoria;
}