package com.fullstack.producto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;

import com.fullstack.producto.model.dto.ProductoDto;
import com.fullstack.producto.model.dto.CrearProductoDto;
import com.fullstack.producto.model.dto.ActualizarProductoDto;
import com.fullstack.producto.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/api")
@Tag(name = "Producto Controller", description = "API para gestión de productos del sistema HuertoHogar")
@CrossOrigin(origins = "*")
@Validated
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "app.database.enabled", 
    havingValue = "true", 
    matchIfMissing = true
)
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Obtener todos los productos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay productos registrados")
    })
    @GetMapping("/productos")
    public ResponseEntity<List<ProductoDto>> obtenerTodosLosProductos() {
        List<ProductoDto> productos = productoService.getAllProductos();
        if (productos == null || productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener productos activos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos activos obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay productos activos")
    })
    @GetMapping("/productos/activos")
    public ResponseEntity<List<ProductoDto>> obtenerProductosActivos() {
        List<ProductoDto> productos = productoService.getProductosActivos();
        if (productos == null || productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener productos disponibles (activos y con stock)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos disponibles obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay productos disponibles")
    })
    @GetMapping("/productos/disponibles")
    public ResponseEntity<List<ProductoDto>> obtenerProductosDisponibles() {
        List<ProductoDto> productos = productoService.getProductosDisponibles();
        if (productos == null || productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener un producto por ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @GetMapping("/productos/{id}")
    public ResponseEntity<ProductoDto> obtenerProductoPorId(
            @Parameter(description = "ID del producto") 
            @PathVariable @NotBlank String id) {
        ProductoDto producto = productoService.getProductoById(id);
        if (producto != null) {
            return ResponseEntity.ok(producto);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener productos por categoría")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos por categoría obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay productos en la categoría especificada")
    })
    @GetMapping("/productos/categoria/{idCategoria}")
    public ResponseEntity<List<ProductoDto>> obtenerProductosPorCategoria(
            @Parameter(description = "ID de la categoría") 
            @PathVariable @NotNull Integer idCategoria) {
        List<ProductoDto> productos = productoService.getProductosPorCategoria(idCategoria);
        if (productos == null || productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener productos orgánicos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos orgánicos obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay productos orgánicos disponibles")
    })
    @GetMapping("/productos/organicos")
    public ResponseEntity<List<ProductoDto>> obtenerProductosOrganicos() {
        List<ProductoDto> productos = productoService.getProductosOrganicos();
        if (productos == null || productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Buscar productos por nombre")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos encontrados"),
        @ApiResponse(responseCode = "204", description = "No se encontraron productos con el nombre especificado")
    })
    @GetMapping("/productos/buscar")
    public ResponseEntity<List<ProductoDto>> buscarProductosPorNombre(
            @Parameter(description = "Nombre o parte del nombre del producto") 
            @RequestParam @NotBlank String nombre) {
        List<ProductoDto> productos = productoService.buscarProductosPorNombre(nombre);
        if (productos == null || productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Obtener productos por rango de precio")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Productos en el rango de precio obtenidos exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay productos en el rango de precio especificado"),
        @ApiResponse(responseCode = "400", description = "Parámetros de precio inválidos")
    })
    @GetMapping("/productos/precio")
    public ResponseEntity<List<ProductoDto>> obtenerProductosPorRangoPrecio(
            @Parameter(description = "Precio mínimo") 
            @RequestParam @Min(value = 1) Integer precioMin,
            @Parameter(description = "Precio máximo") 
            @RequestParam @Min(value = 1) Integer precioMax) {
        
        if (precioMin > precioMax) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ProductoDto> productos = productoService.getProductosPorRangoPrecio(precioMin, precioMax);
        if (productos == null || productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Crear un nuevo producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos del producto inválidos"),
        @ApiResponse(responseCode = "409", description = "El producto ya existe")
    })
    @PostMapping("/productos")
    public ResponseEntity<ProductoDto> crearProducto(@Valid @RequestBody CrearProductoDto crearProductoDto) {
        try {
            ProductoDto nuevoProducto = productoService.crearProducto(crearProductoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Actualizar un producto existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto actualizado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos del producto inválidos"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PutMapping("/productos/{id}")
    public ResponseEntity<ProductoDto> actualizarProducto(
            @Parameter(description = "ID del producto a actualizar") 
            @PathVariable @NotBlank String id,
            @Valid @RequestBody ActualizarProductoDto actualizarProductoDto) {
        try {
            ProductoDto productoActualizado = productoService.actualizarProducto(id, actualizarProductoDto);
            return ResponseEntity.ok(productoActualizado);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Operation(summary = "Eliminar un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @DeleteMapping("/productos/{id}")
    public ResponseEntity<Void> eliminarProducto(
            @Parameter(description = "ID del producto a eliminar") 
            @PathVariable @NotBlank String id) {
        boolean eliminado = productoService.eliminarProducto(id);
        if (eliminado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Desactivar un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto desactivado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    @PatchMapping("/productos/{id}/desactivar")
    public ResponseEntity<Void> desactivarProducto(
            @Parameter(description = "ID del producto a desactivar") 
            @PathVariable @NotBlank String id) {
        boolean desactivado = productoService.desactivarProducto(id);
        if (desactivado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Actualizar stock de un producto")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "400", description = "Cantidad de stock inválida")
    })
    @PatchMapping("/productos/{id}/stock")
    public ResponseEntity<Void> actualizarStock(
            @Parameter(description = "ID del producto") 
            @PathVariable @NotBlank String id,
            @Parameter(description = "Nuevo stock del producto") 
            @RequestParam @Min(0) Integer stock) {
        boolean actualizado = productoService.actualizarStock(id, stock);
        if (actualizado) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Obtener productos con stock bajo")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos con stock bajo obtenida exitosamente"),
        @ApiResponse(responseCode = "204", description = "No hay productos con stock bajo")
    })
    @GetMapping("/productos/stock-bajo")
    public ResponseEntity<List<ProductoDto>> obtenerProductosConStockBajo(
            @Parameter(description = "Stock mínimo para considerar como bajo") 
            @RequestParam(defaultValue = "10") @Min(0) Integer stockMinimo) {
        List<ProductoDto> productos = productoService.getProductosConStockBajo(stockMinimo);
        if (productos == null || productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(productos);
    }

    @Operation(summary = "Health check para el servicio de productos")
    @GetMapping("/productos/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Servicio de productos funcionando correctamente en puerto 8082");
    }
}