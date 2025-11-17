package com.fullstack.producto.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.fullstack.producto.model.entity.ProductoEntity;

@Repository
public interface ProductoRepository extends MongoRepository<ProductoEntity, String> {

    Optional<ProductoEntity> findById(String id);
    
    boolean existsById(String id);
    
    List<ProductoEntity> findByIdCategoria(Integer idCategoria);
    
    List<ProductoEntity> findByEstaActivoOrderByNombreAsc(String estaActivo);
    
    List<ProductoEntity> findByCertificacionOrganica(String certificacionOrganica);
    
    @Query("{ 'nombre': { $regex: ?0, $options: 'i' } }")
    List<ProductoEntity> findByNombreContaining(String nombre);
    
    @Query("{ 'precio': { $gte: ?0, $lte: ?1 } }")
    List<ProductoEntity> findByPrecioBetween(Integer precioMin, Integer precioMax);
    
    @Query("{ 'stock': { $gt: 0 }, 'esta_activo': 'S' }")
    List<ProductoEntity> findProductosDisponibles();
    
    @Query("{ 'stock': { $lte: ?0 } }")
    List<ProductoEntity> findProductosConStockBajo(Integer stockMinimo);
    
    @Query("{ 'id_categoria': ?0, 'esta_activo': 'S' }")
    List<ProductoEntity> findProductosActivosPorCategoria(Integer idCategoria);
    
    @Query("{ 'certificacion_organica': 'S', 'esta_activo': 'S' }")
    List<ProductoEntity> findProductosOrganicosActivos();
    
    @Query("{ 'origen': ?0, 'esta_activo': 'S' }")
    List<ProductoEntity> findByOrigenAndActivo(String origen);
    
    @Query(value = "{ 'id_categoria': ?0 }", count = true)
    Long countByIdCategoria(Integer idCategoria);
}