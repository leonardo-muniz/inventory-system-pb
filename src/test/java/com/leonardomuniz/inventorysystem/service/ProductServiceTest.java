package com.leonardomuniz.inventorysystem.service;

import com.leonardomuniz.inventorysystem.dto.ProductDTO;
import com.leonardomuniz.inventorysystem.exception.BusinessException;
import com.leonardomuniz.inventorysystem.exception.ResourceNotFoundException;
import com.leonardomuniz.inventorysystem.model.PhysicalProduct;
import com.leonardomuniz.inventorysystem.model.Product;
import com.leonardomuniz.inventorysystem.repository.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository repository;

    @InjectMocks
    private ProductService service;

    private ProductDTO dto;
    private Product product;

    @BeforeEach
    void setup() {
        // ProductDTO com 7 campos: id, name, description, price, quantity, category, updatedAt
        dto = new ProductDTO(1L, "Laptop", "Gaming laptop", new BigDecimal("5000"), 5, "PHYSICAL", null);

        product = new PhysicalProduct("Laptop", "Gaming laptop", new BigDecimal("5000"), 5);
        ReflectionTestUtils.setField(product, "id", 1L);
    }

    @Test
    @DisplayName("Deve criar um produto com sucesso quando os dados são válidos")
    void shouldCreateProductSuccessfully() {
        when(repository.existsByNameIgnoreCase(dto.name())).thenReturn(false);
        when(repository.save(any(Product.class))).thenReturn(product);

        Product saved = service.create(dto);

        assertNotNull(saved);
        assertEquals("Laptop", saved.getName());
        verify(repository).save(any(Product.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar produto com nome já existente")
    void shouldThrowWhenNameAlreadyExists() {
        when(repository.existsByNameIgnoreCase(dto.name())).thenReturn(true);
        assertThrows(BusinessException.class, () -> service.create(dto));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar criar produto com preço acima do limite permitido")
    void shouldThrowWhenPriceExceedsLimit() {
        ProductDTO invalidDto = new ProductDTO(null, dto.name(), dto.description(), new BigDecimal("2000000"), dto.quantity(), dto.category(), null);
        when(repository.existsByNameIgnoreCase(invalidDto.name())).thenReturn(false);

        assertThrows(BusinessException.class, () -> service.create(invalidDto));
    }

    @Test
    @DisplayName("Deve encontrar um produto por ID com sucesso")
    void shouldFindProductById() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        Product found = service.findById(1L);
        assertEquals("Laptop", found.getName());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o produto não for encontrado pelo ID")
    void shouldThrowWhenProductNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.findById(1L));
    }

    @Test
    @DisplayName("Deve atualizar um produto com sucesso")
    void shouldUpdateProductSuccessfully() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        when(repository.save(any(Product.class))).thenReturn(product);

        Product updated = service.update(1L, dto);

        assertEquals("Laptop", updated.getName());
        verify(repository).save(product);
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar produto para um nome que já pertence a outro produto")
    void shouldThrowWhenUpdatingWithDuplicateName() {
        Product another = new PhysicalProduct("Phone", "Smartphone", new BigDecimal("1000"), 10);
        ReflectionTestUtils.setField(another, "id", 2L);

        when(repository.findById(1L)).thenReturn(Optional.of(another));
        when(repository.existsByNameIgnoreCase(dto.name())).thenReturn(true);

        assertThrows(BusinessException.class, () -> service.update(1L, dto));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar quantidade acima do limite permitido")
    void shouldThrowWhenQuantityExceedsLimit() {
        ProductDTO invalidDto = new ProductDTO(1L, dto.name(), dto.description(), dto.price(), 20000, dto.category(), null);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(BusinessException.class, () -> service.update(1L, invalidDto));
    }

    @Test
    @DisplayName("Deve impedir a exclusão de um produto que ainda possui estoque disponível")
    void shouldNotDeleteWhenStockAvailable() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));
        assertThrows(BusinessException.class, () -> service.delete(1L));
        verify(repository, never()).delete(any());
    }

    @Test
    @DisplayName("Deve permitir a exclusão de um produto quando o estoque estiver zerado")
    void shouldDeleteWhenStockIsZero() {
        product.updateStock(0);
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        service.delete(1L);

        verify(repository).delete(product);
    }

    @Test
    @DisplayName("Deve permitir a alteração do nome do produto para um novo nome válido")
    void shouldUpdateProductWhenChangingToValidNewName() {
        when(repository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO newDto = new ProductDTO(1L, "Desktop", dto.description(), dto.price(), dto.quantity(), dto.category(), null);

        when(repository.existsByNameIgnoreCase("Desktop")).thenReturn(false);
        when(repository.save(any(Product.class))).thenReturn(product);

        Product updated = service.update(1L, newDto);

        assertEquals("Desktop", updated.getName());
        verify(repository).save(product);
    }
}