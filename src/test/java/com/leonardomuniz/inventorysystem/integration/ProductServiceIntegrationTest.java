package com.leonardomuniz.inventorysystem.integration;

import com.leonardomuniz.inventorysystem.dto.ProductDTO;
import com.leonardomuniz.inventorysystem.exception.BusinessException;
import com.leonardomuniz.inventorysystem.model.Product;
import com.leonardomuniz.inventorysystem.repository.ProductRepository;
import com.leonardomuniz.inventorysystem.service.ProductService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductServiceIntegrationTest {

    @Autowired
    private ProductService service;

    @Autowired
    private ProductRepository repository;

    @BeforeEach
    void cleanDatabase() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve persistir um produto no banco de dados com sucesso via Service")
    void shouldCreateProductSuccessfully() {
        // Ordem: id, name, description, price, quantity, category, updatedAt
        ProductDTO dto = new ProductDTO(null, "Laptop", "Gaming machine", new BigDecimal("5000"), 10, "PHYSICAL", null);
        Product saved = service.create(dto);

        assertNotNull(saved.getId());
        assertEquals("Laptop", saved.getName());
        assertEquals(1, repository.count());
    }

    @Test
    @DisplayName("Deve impedir a criação de duplicatas no banco de dados lançando BusinessException")
    void shouldNotAllowDuplicateName() {
        ProductDTO dto = new ProductDTO(null, "Laptop", "Gaming machine", new BigDecimal("5000"), 10, "PHYSICAL", null);
        service.create(dto);

        ProductDTO duplicate = new ProductDTO(null, "Laptop", "Gaming machine", new BigDecimal("6000"), 5, "PHYSICAL", null);
        assertThrows(BusinessException.class, () -> service.create(duplicate));
    }

    @Test
    @DisplayName("Deve recuperar todos os produtos persistidos no banco")
    void shouldFindAllProducts() {
        ProductDTO dto = new ProductDTO(null, "Mouse", "Accessories", new BigDecimal("100"), 50, "PHYSICAL", null);
        service.create(dto);

        List<Product> products = service.findAll();
        assertEquals(1, products.size());
    }

    @Test
    @DisplayName("Deve excluir um produto do banco quando o estoque for zero")
    void shouldDeleteProductWhenNoStock() {
        ProductDTO dto = new ProductDTO(null, "Keyboard", "Accessories", new BigDecimal("200"), 0, "PHYSICAL", null);
        Product product = service.create(dto);

        service.delete(product.getId());

        assertEquals(0, repository.count());
    }

    @Test
    @DisplayName("Deve lançar exceção e não excluir do banco se o produto possuir estoque")
    void shouldNotDeleteProductWithStock() {
        ProductDTO dto = new ProductDTO(null, "Monitor", "Electronics", new BigDecimal("1500"), 5, "PHYSICAL", null);
        Product product = service.create(dto);

        assertThrows(BusinessException.class, () -> service.delete(product.getId()));
    }
}
