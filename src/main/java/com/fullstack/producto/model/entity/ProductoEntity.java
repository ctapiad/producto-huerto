package com.fullstack.producto.model.entity;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;

@Document(collection = "producto")
@Data
public class ProductoEntity {

    @Id
    private String id; // MongoDB usa _id por defecto

    @Field("nombre")
    private String nombre;

    @Field("link_imagen")
    private String linkImagen;

    @Field("descripcion")
    private String descripcion;

    @Field("precio")
    private Integer precio; // MongoDB usa Integer según los datos

    @Field("stock")
    private Integer stock;

    @Field("origen")
    private String origen;

    @Field("certificacion_organica")
    private String certificacionOrganica;

    @Field("esta_activo")
    private String estaActivo;

    @Field("fecha_ingreso")
    private Date fechaIngreso;

    @Field("id_categoria")
    private Integer idCategoria; // MongoDB usa Integer según los datos

    // Constructor por defecto
    public ProductoEntity() {
        this.fechaIngreso = new Date(); // Asigna fecha actual por defecto
        this.estaActivo = "S"; // Por defecto el producto está activo
        this.certificacionOrganica = "N"; // Por defecto no es orgánico
    }

    // Constructor con parámetros principales
    public ProductoEntity(String id, String nombre, String descripcion, 
                         Integer precio, Integer stock, Integer idCategoria) {
        this();
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.idCategoria = idCategoria;
    }

    // Métodos de utilidad
    public boolean isOrganico() {
        return "S".equals(this.certificacionOrganica);
    }

    public void setOrganico(boolean organico) {
        this.certificacionOrganica = organico ? "S" : "N";
    }

    public boolean isActivo() {
        return "S".equals(this.estaActivo);
    }

    public void setActivo(boolean activo) {
        this.estaActivo = activo ? "S" : "N";
    }

    public boolean tieneStock() {
        return this.stock != null && this.stock > 0;
    }

    public void reducirStock(int cantidad) {
        if (this.stock != null && this.stock >= cantidad) {
            this.stock -= cantidad;
        } else {
            throw new IllegalArgumentException("Stock insuficiente");
        }
    }

    public void aumentarStock(int cantidad) {
        if (this.stock == null) {
            this.stock = cantidad;
        } else {
            this.stock += cantidad;
        }
    }
}