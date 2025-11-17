package com.fullstack.producto;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fullstack.producto.model.dto.ProductoDto;
import com.fullstack.producto.model.dto.CrearProductoDto;
import com.fullstack.producto.model.entity.ProductoEntity;
import com.fullstack.producto.repository.ProductoRepository;
import com.fullstack.producto.service.ProductoService;

public class ProductoTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private ProductoService productoService;

    private ProductoEntity productoEntity;
    private CrearProductoDto crearProductoDto;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        
        productoEntity = new ProductoEntity();
        productoEntity.setId("FR001");
        productoEntity.setNombre("Manzanas Fuji");
        productoEntity.setDescripcion("Manzanas crujientes y dulces del Valle del Maule");
        productoEntity.setPrecio(1200);
        productoEntity.setStock(150);
        productoEntity.setOrigen("Valle del Maule");
        productoEntity.setOrganico(false);
        productoEntity.setActivo(true);
        productoEntity.setFechaIngreso(new Date());
        productoEntity.setIdCategoria(1);

        crearProductoDto = new CrearProductoDto();
        crearProductoDto.setIdProducto("FR002");
        crearProductoDto.setNombre("Naranjas Valencia");
        crearProductoDto.setDescripcion("Jugosas y ricas en vitamina C");
        crearProductoDto.setPrecio(1000);
        crearProductoDto.setStock(200);
        crearProductoDto.setCertificacionOrganica(false);
        crearProductoDto.setEstaActivo(true);
        crearProductoDto.setIdCategoria(1);
    }

    @Test
    public void testGetProductoById() {
        // Given
        when(productoRepository.findById("FR001")).thenReturn(Optional.of(productoEntity));

        // When
        ProductoDto resultado = productoService.getProductoById("FR001");

        // Then
        assertNotNull(resultado);
        assertEquals("FR001", resultado.getIdProducto());
        assertEquals("Manzanas Fuji", resultado.getNombre());
        assertEquals(1200, resultado.getPrecio());
        assertEquals(150, resultado.getStock());
        assertTrue(resultado.isEstaActivo());
        assertFalse(resultado.isCertificacionOrganica());
    }

    @Test
    public void testGetProductoById_NotFound() {
        // Given
        when(productoRepository.findById("INEXISTENTE")).thenReturn(Optional.empty());

        // When
        ProductoDto resultado = productoService.getProductoById("INEXISTENTE");

        // Then
        assertNull(resultado);
    }

    @Test
    public void testCrearProducto() {
        // Given
        when(productoRepository.existsById("FR002")).thenReturn(false);
        when(productoRepository.save(any(ProductoEntity.class))).thenReturn(productoEntity);

        // When
        ProductoDto resultado = productoService.crearProducto(crearProductoDto);

        // Then
        assertNotNull(resultado);
        assertEquals("FR001", resultado.getIdProducto());
        assertEquals("Manzanas Fuji", resultado.getNombre());
    }

    @Test
    public void testCrearProducto_YaExiste() {
        // Given
        when(productoRepository.existsById("FR002")).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productoService.crearProducto(crearProductoDto);
        });
        
        assertTrue(exception.getMessage().contains("Ya existe un producto con el ID"));
    }

    @Test
    public void testGetAllProductos() {
        // Given
        List<ProductoEntity> listaProductos = new ArrayList<>();
        listaProductos.add(productoEntity);
        when(productoRepository.findAll()).thenReturn(listaProductos);

        // When
        List<ProductoDto> resultado = productoService.getAllProductos();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("FR001", resultado.get(0).getIdProducto());
    }

    @Test
    public void testGetProductosDisponibles() {
        // Given
        List<ProductoEntity> listaProductos = new ArrayList<>();
        listaProductos.add(productoEntity);
        when(productoRepository.findProductosDisponibles()).thenReturn(listaProductos);

        // When
        List<ProductoDto> resultado = productoService.getProductosDisponibles();

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("FR001", resultado.get(0).getIdProducto());
        assertTrue(resultado.get(0).isEstaActivo());
        assertTrue(resultado.get(0).getStock() > 0);
    }

    @Test
    public void testBuscarProductosPorNombre() {
        // Given
        List<ProductoEntity> listaProductos = new ArrayList<>();
        listaProductos.add(productoEntity);
        when(productoRepository.findByNombreContaining("Manzana")).thenReturn(listaProductos);

        // When
        List<ProductoDto> resultado = productoService.buscarProductosPorNombre("Manzana");

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Manzanas Fuji", resultado.get(0).getNombre());
    }

    @Test
    public void testGetProductosPorCategoria() {
        // Given
        List<ProductoEntity> listaProductos = new ArrayList<>();
        listaProductos.add(productoEntity);
        when(productoRepository.findProductosActivosPorCategoria(1)).thenReturn(listaProductos);

        // When
        List<ProductoDto> resultado = productoService.getProductosPorCategoria(1);

        // Then
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(Integer.valueOf(1), resultado.get(0).getIdCategoria());
    }

    @Test
    public void testActualizarStock() {
        // Given
        when(productoRepository.findById("FR001")).thenReturn(Optional.of(productoEntity));
        when(productoRepository.save(any(ProductoEntity.class))).thenReturn(productoEntity);

        // When
        boolean resultado = productoService.actualizarStock("FR001", 100);

        // Then
        assertTrue(resultado);
    }

    @Test
    public void testDesactivarProducto() {
        // Given
        when(productoRepository.findById("FR001")).thenReturn(Optional.of(productoEntity));
        when(productoRepository.save(any(ProductoEntity.class))).thenReturn(productoEntity);

        // When
        boolean resultado = productoService.desactivarProducto("FR001");

        // Then
        assertTrue(resultado);
    }

    @Test
    public void testEliminarProducto() {
        // Given
        when(productoRepository.findById("FR001")).thenReturn(Optional.of(productoEntity));

        // When
        boolean resultado = productoService.eliminarProducto("FR001");

        // Then
        assertTrue(resultado);
    }

    @Test
    public void testProductoEntity_MetodosUtilidad() {
        // Test isOrganico
        assertFalse(productoEntity.isOrganico());
        productoEntity.setOrganico(true);
        assertTrue(productoEntity.isOrganico());

        // Test isActivo
        assertTrue(productoEntity.isActivo());
        productoEntity.setActivo(false);
        assertFalse(productoEntity.isActivo());

        // Test tieneStock
        assertTrue(productoEntity.tieneStock());
        productoEntity.setStock(0);
        assertFalse(productoEntity.tieneStock());

        // Test reducirStock
        productoEntity.setStock(100);
        productoEntity.reducirStock(50);
        assertEquals(50, productoEntity.getStock());

        // Test aumentarStock
        productoEntity.aumentarStock(25);
        assertEquals(75, productoEntity.getStock());

        // Test reducirStock con stock insuficiente
        assertThrows(IllegalArgumentException.class, () -> {
            productoEntity.reducirStock(100);
        });
    }
}