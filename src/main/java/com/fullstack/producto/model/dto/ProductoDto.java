package com.fullstack.producto.model.dto;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductoDto {
    private String idProducto;
    private String nombre;
    private String linkImagen;
    private String descripcion;
    private Integer precio;
    private Integer stock;
    private String origen;
    private boolean certificacionOrganica;
    private boolean estaActivo;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date fechaIngreso;
    private Integer idCategoria;
}
