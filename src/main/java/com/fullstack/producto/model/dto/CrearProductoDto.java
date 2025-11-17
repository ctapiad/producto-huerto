package com.fullstack.producto.model.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CrearProductoDto {
    
    @NotBlank(message = "El ID del producto es obligatorio")
    @Size(max = 10, message = "El ID del producto no puede tener más de 10 caracteres")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{3}$", message = "El ID debe seguir el formato XX000 (2 letras mayúsculas + 3 números)")
    private String idProducto;
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 100, message = "El nombre no puede tener más de 100 caracteres")
    private String nombre;
    
    @Size(max = 255, message = "El link de imagen no puede tener más de 255 caracteres")
    @Pattern(regexp = "^(https?://).*", message = "El link de imagen debe ser una URL válida")
    private String linkImagen;
    
    @Size(max = 500, message = "La descripción no puede tener más de 500 caracteres")
    private String descripcion;
    
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 1, message = "El precio debe ser mayor a 0")
    private Integer precio;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    private Integer stock;
    
    @Size(max = 100, message = "El origen no puede tener más de 100 caracteres")
    private String origen;
    
    private boolean certificacionOrganica = false;
    
    private boolean estaActivo = true;
    
    @NotNull(message = "La categoría es obligatoria")
    private Integer idCategoria;
}