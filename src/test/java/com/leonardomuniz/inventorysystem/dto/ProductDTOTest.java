package com.leonardomuniz.inventorysystem.dto;

import com.leonardomuniz.inventorysystem.model.PhysicalProduct;
import com.leonardomuniz.inventorysystem.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductDTOTest {

    @Test
    @DisplayName("Deve validar o estado do Record (imutabilidade e campos)")
    void shouldTestRecordState() {
        // Arrange & Act
        BigDecimal price = new BigDecimal("199.99");
        LocalDateTime now = LocalDateTime.now();

        // ProductDTO com 7 campos conforme sua definição atual
        ProductDTO dto = new ProductDTO(
                1L,
                "Monitor",
                "Monitor 4K",
                price,
                15,
                "PHYSICAL",
                now
        );

        // Assert
        assertEquals(1L, dto.id());
        assertEquals("Monitor", dto.name());
        assertEquals("Monitor 4K", dto.description());
        assertEquals(price, dto.price());
        assertEquals(15, dto.quantity());
        assertEquals("PHYSICAL", dto.category());
        assertEquals(now, dto.updatedAt());
    }

    @Test
    @DisplayName("Deve converter uma Entidade para DTO com sucesso")
    void shouldConvertFromEntitySuccessfully() {
        // Arrange
        BigDecimal price = new BigDecimal("150.00");
        Product product = new PhysicalProduct(
                "Keyboard",
                "Mechanical Keyboard",
                price,
                50
        );
        ReflectionTestUtils.setField(product, "id", 10L);

        // Act
        ProductDTO dto = ProductDTO.fromEntity(product);

        // Assert
        assertEquals(10L, dto.id());
        assertEquals(product.getName(), dto.name());
        assertEquals(product.getDescription(), dto.description());
        assertEquals(product.getPrice(), dto.price());
        assertEquals(product.getQuantity(), dto.quantity());
        assertEquals(product.getCategoryType(), dto.category());
    }
}
