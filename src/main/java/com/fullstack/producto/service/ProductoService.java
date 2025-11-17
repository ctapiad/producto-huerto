package com.fullstack.producto.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fullstack.producto.model.dto.ProductoDto;
import com.fullstack.producto.model.dto.CrearProductoDto;
import com.fullstack.producto.model.dto.ActualizarProductoDto;
import com.fullstack.producto.model.entity.ProductoEntity;
import com.fullstack.producto.repository.ProductoRepository;

@Service
@org.springframework.boot.autoconfigure.condition.ConditionalOnProperty(
    name = "app.database.enabled", 
    havingValue = "true", 
    matchIfMissing = true
)
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public ProductoService() {
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> getAllProductos() {
        try {
            List<ProductoEntity> listaProductos = productoRepository.findAll();

            if (listaProductos.isEmpty()) {
                System.out.println("No hay productos registrados en la base de datos");
                return new ArrayList<>();
            } 

            List<ProductoDto> productos = new ArrayList<>();
            for (ProductoEntity productoEntity : listaProductos) {
                productos.add(convertirEntityAProductoDto(productoEntity));
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error al obtener los productos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> getProductosActivos() {
        try {
            List<ProductoEntity> listaProductos = productoRepository.findByEstaActivoOrderByNombreAsc("S");
            
            List<ProductoDto> productos = new ArrayList<>();
            for (ProductoEntity productoEntity : listaProductos) {
                productos.add(convertirEntityAProductoDto(productoEntity));
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error al obtener los productos activos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> getProductosDisponibles() {
        try {
            List<ProductoEntity> listaProductos = productoRepository.findProductosDisponibles();
            
            List<ProductoDto> productos = new ArrayList<>();
            for (ProductoEntity productoEntity : listaProductos) {
                productos.add(convertirEntityAProductoDto(productoEntity));
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error al obtener los productos disponibles: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> getProductosPorCategoria(Integer idCategoria) {
        try {
            List<ProductoEntity> listaProductos = productoRepository.findProductosActivosPorCategoria(idCategoria);
            
            List<ProductoDto> productos = new ArrayList<>();
            for (ProductoEntity productoEntity : listaProductos) {
                productos.add(convertirEntityAProductoDto(productoEntity));
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error al obtener los productos por categoría: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> getProductosOrganicos() {
        try {
            List<ProductoEntity> listaProductos = productoRepository.findProductosOrganicosActivos();
            
            List<ProductoDto> productos = new ArrayList<>();
            for (ProductoEntity productoEntity : listaProductos) {
                productos.add(convertirEntityAProductoDto(productoEntity));
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error al obtener los productos orgánicos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> buscarProductosPorNombre(String nombre) {
        try {
            List<ProductoEntity> listaProductos = productoRepository.findByNombreContaining(nombre);
            
            List<ProductoDto> productos = new ArrayList<>();
            for (ProductoEntity productoEntity : listaProductos) {
                productos.add(convertirEntityAProductoDto(productoEntity));
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error al buscar productos por nombre: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> getProductosPorRangoPrecio(Integer precioMin, Integer precioMax) {
        try {
            List<ProductoEntity> listaProductos = productoRepository.findByPrecioBetween(precioMin, precioMax);
            
            List<ProductoDto> productos = new ArrayList<>();
            for (ProductoEntity productoEntity : listaProductos) {
                productos.add(convertirEntityAProductoDto(productoEntity));
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error al obtener productos por rango de precio: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public ProductoDto getProductoById(String idProducto) {
        try {
            Optional<ProductoEntity> productoEntity = productoRepository.findById(idProducto);
            if (productoEntity.isPresent()) {
                return convertirEntityAProductoDto(productoEntity.get());
            } else {
                System.out.println("Producto no encontrado con ID: " + idProducto);
                return null;
            }
        } catch (Exception e) {
            System.out.println("Error al obtener el producto por ID: " + e.getMessage());
            return null;
        }
    }

    @Transactional
    public ProductoDto crearProducto(CrearProductoDto crearProductoDto) {
        try {
            // Verificar que el ID del producto no exista
            if (productoRepository.existsById(crearProductoDto.getIdProducto())) {
                throw new IllegalArgumentException("Ya existe un producto con el ID: " + crearProductoDto.getIdProducto());
            }

            ProductoEntity productoEntity = new ProductoEntity();
            productoEntity.setId(crearProductoDto.getIdProducto());
            productoEntity.setNombre(crearProductoDto.getNombre());
            productoEntity.setLinkImagen(crearProductoDto.getLinkImagen());
            productoEntity.setDescripcion(crearProductoDto.getDescripcion());
            productoEntity.setPrecio(crearProductoDto.getPrecio());
            productoEntity.setStock(crearProductoDto.getStock());
            productoEntity.setOrigen(crearProductoDto.getOrigen());
            productoEntity.setOrganico(crearProductoDto.isCertificacionOrganica());
            productoEntity.setActivo(crearProductoDto.isEstaActivo());
            productoEntity.setFechaIngreso(new Date());
            productoEntity.setIdCategoria(crearProductoDto.getIdCategoria());

            ProductoEntity productoGuardado = productoRepository.save(productoEntity);
            return convertirEntityAProductoDto(productoGuardado);

        } catch (IllegalArgumentException e) {
            System.out.println("Error de validación: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error al crear el producto: " + e.getMessage());
            throw new RuntimeException("Error interno al crear el producto", e);
        }
    }

    @Transactional
    public ProductoDto actualizarProducto(String idProducto, ActualizarProductoDto actualizarProductoDto) {
        try {
            Optional<ProductoEntity> productoOptional = productoRepository.findById(idProducto);
            
            if (productoOptional.isEmpty()) {
                throw new IllegalArgumentException("Producto no encontrado con ID: " + idProducto);
            }

            ProductoEntity productoEntity = productoOptional.get();
            
            // Solo actualizar los campos que no sean null
            if (actualizarProductoDto.getNombre() != null) {
                productoEntity.setNombre(actualizarProductoDto.getNombre());
            }
            if (actualizarProductoDto.getLinkImagen() != null) {
                productoEntity.setLinkImagen(actualizarProductoDto.getLinkImagen());
            }
            if (actualizarProductoDto.getDescripcion() != null) {
                productoEntity.setDescripcion(actualizarProductoDto.getDescripcion());
            }
            if (actualizarProductoDto.getPrecio() != null) {
                productoEntity.setPrecio(actualizarProductoDto.getPrecio());
            }
            if (actualizarProductoDto.getStock() != null) {
                productoEntity.setStock(actualizarProductoDto.getStock());
            }
            if (actualizarProductoDto.getOrigen() != null) {
                productoEntity.setOrigen(actualizarProductoDto.getOrigen());
            }
            if (actualizarProductoDto.getCertificacionOrganica() != null) {
                productoEntity.setOrganico(actualizarProductoDto.getCertificacionOrganica());
            }
            if (actualizarProductoDto.getEstaActivo() != null) {
                productoEntity.setActivo(actualizarProductoDto.getEstaActivo());
            }
            if (actualizarProductoDto.getIdCategoria() != null) {
                productoEntity.setIdCategoria(actualizarProductoDto.getIdCategoria());
            }

            ProductoEntity productoActualizado = productoRepository.save(productoEntity);
            return convertirEntityAProductoDto(productoActualizado);

        } catch (IllegalArgumentException e) {
            System.out.println("Error de validación: " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.out.println("Error al actualizar el producto: " + e.getMessage());
            throw new RuntimeException("Error interno al actualizar el producto", e);
        }
    }

    @Transactional
    public boolean eliminarProducto(String idProducto) {
        try {
            Optional<ProductoEntity> productoOptional = productoRepository.findById(idProducto);
            
            if (productoOptional.isEmpty()) {
                System.out.println("Producto no encontrado con ID: " + idProducto);
                return false;
            }

            productoRepository.delete(productoOptional.get());
            System.out.println("Producto eliminado exitosamente con ID: " + idProducto);
            return true;

        } catch (Exception e) {
            System.out.println("Error al eliminar el producto: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean desactivarProducto(String idProducto) {
        try {
            Optional<ProductoEntity> productoOptional = productoRepository.findById(idProducto);
            
            if (productoOptional.isEmpty()) {
                throw new IllegalArgumentException("Producto no encontrado con ID: " + idProducto);
            }

            ProductoEntity producto = productoOptional.get();
            producto.setActivo(false);
            productoRepository.save(producto);
            
            System.out.println("Producto desactivado exitosamente con ID: " + idProducto);
            return true;

        } catch (Exception e) {
            System.out.println("Error al desactivar el producto: " + e.getMessage());
            return false;
        }
    }

    @Transactional
    public boolean actualizarStock(String idProducto, Integer nuevoStock) {
        try {
            Optional<ProductoEntity> productoOptional = productoRepository.findById(idProducto);
            
            if (productoOptional.isEmpty()) {
                throw new IllegalArgumentException("Producto no encontrado con ID: " + idProducto);
            }

            ProductoEntity producto = productoOptional.get();
            producto.setStock(nuevoStock);
            productoRepository.save(producto);
            
            System.out.println("Stock actualizado exitosamente para producto ID: " + idProducto);
            return true;

        } catch (Exception e) {
            System.out.println("Error al actualizar el stock: " + e.getMessage());
            return false;
        }
    }

    @Transactional(readOnly = true)
    public List<ProductoDto> getProductosConStockBajo(Integer stockMinimo) {
        try {
            List<ProductoEntity> listaProductos = productoRepository.findProductosConStockBajo(stockMinimo);
            
            List<ProductoDto> productos = new ArrayList<>();
            for (ProductoEntity productoEntity : listaProductos) {
                productos.add(convertirEntityAProductoDto(productoEntity));
            }
            return productos;
        } catch (Exception e) {
            System.out.println("Error al obtener productos con stock bajo: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Método privado para convertir Entity a DTO
    private ProductoDto convertirEntityAProductoDto(ProductoEntity productoEntity) {
        ProductoDto productoDto = new ProductoDto();
        productoDto.setIdProducto(productoEntity.getId());
        productoDto.setNombre(productoEntity.getNombre());
        productoDto.setLinkImagen(productoEntity.getLinkImagen());
        productoDto.setDescripcion(productoEntity.getDescripcion());
        productoDto.setPrecio(productoEntity.getPrecio());
        productoDto.setStock(productoEntity.getStock());
        productoDto.setOrigen(productoEntity.getOrigen());
        productoDto.setCertificacionOrganica(productoEntity.isOrganico());
        productoDto.setEstaActivo(productoEntity.isActivo());
        productoDto.setFechaIngreso(productoEntity.getFechaIngreso());
        productoDto.setIdCategoria(productoEntity.getIdCategoria());
        
        return productoDto;
    }
}