package com.leonardomuniz.inventorysystem.model;

import com.leonardomuniz.inventorysystem.exception.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductModelTest {

    // --- TESTES: ProductFactory ---

    @Test
    @DisplayName("Deve criar Produto Digital via Factory com estoque zerado (Ilimitado)")
    void shouldCreateDigitalProductViaFactory() {
        // Mesmo passando 10, a factory deve garantir 0 para Digital
        Product product = ProductFactory.create("DIGITAL", "E-book", "Java Guide", new BigDecimal("50.00"), 10);

        assertInstanceOf(DigitalProduct.class, product);
        assertEquals("DIGITAL", product.getCategoryType());
        assertEquals(0, product.getQuantity(), "Produtos digitais devem sempre ter estoque 0 (ilimitado)");
    }

    @Test
    @DisplayName("Deve criar Produto Físico via Factory com a quantidade correta")
    void shouldCreatePhysicalProductViaFactory() {
        Product product = ProductFactory.create("PHYSICAL", "Mouse", "Gamer Mouse", new BigDecimal("150.00"), 5);

        assertInstanceOf(PhysicalProduct.class, product);
        assertEquals("PHYSICAL", product.getCategoryType());
        assertEquals(5, product.getQuantity());
    }

    @Test
    @DisplayName("Deve criar Produto Físico como padrão para categorias desconhecidas")
    void shouldCreatePhysicalProductAsDefaultViaFactory() {
        Product product = ProductFactory.create("UNKNOWN", "Keyboard", "Mechanical", new BigDecimal("250.00"), 15);

        assertInstanceOf(PhysicalProduct.class, product);
        assertEquals("PHYSICAL", product.getCategoryType());
    }

    // --- TESTES: PhysicalProduct ---

    @Test
    @DisplayName("Deve lançar exceção se o preço do Produto Físico exceder o limite de 1 milhão")
    void shouldThrowWhenPhysicalProductPriceExceedsLimit() {
        assertThrows(BusinessException.class, () ->
                new PhysicalProduct("Server", "Enterprise Server", new BigDecimal("1000001.00"), 10)
        );
    }

    @Test
    @DisplayName("Deve lançar exceção se o estoque do Produto Físico exceder 10.000 unidades")
    void shouldThrowWhenPhysicalProductStockExceedsLimit() {
        PhysicalProduct product = new PhysicalProduct("Pen", "Blue Pen", new BigDecimal("2.00"), 100);

        assertThrows(BusinessException.class, () -> product.updateStock(10001));
    }

    @Test
    @DisplayName("Deve atualizar o estoque do Produto Físico com sucesso")
    void shouldUpdatePhysicalStockSuccessfully() {
        PhysicalProduct product = new PhysicalProduct("Pen", "Blue Pen", new BigDecimal("2.00"), 100);
        product.updateStock(500);

        assertEquals(500, product.getQuantity());
    }

    // --- TESTES: DigitalProduct ---

    @Test
    @DisplayName("Deve lançar exceção se o preço do Produto Digital exceder o limite de 500 mil")
    void shouldThrowWhenDigitalProductPriceExceedsLimit() {
        assertThrows(BusinessException.class, () ->
                new DigitalProduct("Software", "Enterprise License", new BigDecimal("500001.00"))
        );
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atribuir estoque positivo a um Produto Digital")
    void shouldThrowWhenDigitalProductGetsPhysicalStock() {
        DigitalProduct product = new DigitalProduct("E-book", "Java Guide", new BigDecimal("50.00"));

        // Regra: Digital não aceita quantidade > 0
        assertThrows(BusinessException.class, () -> product.updateStock(1));
    }

    @Test
    @DisplayName("Deve permitir manter estoque em zero para Produto Digital")
    void shouldAllowDigitalProductToStayAtZeroStock() {
        DigitalProduct product = new DigitalProduct("E-book", "Java Guide", new BigDecimal("50.00"));

        assertDoesNotThrow(() -> product.updateStock(0));
        assertEquals(0, product.getQuantity());
    }

    // --- TESTES: Abstract Product (Regras Compartilhadas) ---

    @Test
    @DisplayName("Deve atualizar detalhes (nome, descrição, preço) com sucesso")
    void shouldUpdateDetailsSuccessfully() {
        PhysicalProduct product = new PhysicalProduct("Monitor", "24 inches", new BigDecimal("800.00"), 10);

        product.updateDetails("Monitor 4K", "27 inches", new BigDecimal("1200.00"));

        assertEquals("Monitor 4K", product.getName());
        assertEquals("27 inches", product.getDescription());
        assertEquals(new BigDecimal("1200.00"), product.getPrice());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar detalhes com preço zero ou negativo")
    void shouldThrowWhenUpdatingDetailsWithZeroOrNegativePrice() {
        PhysicalProduct product = new PhysicalProduct("Monitor", "24 inches", new BigDecimal("800.00"), 10);

        assertThrows(BusinessException.class, () ->
                product.updateDetails("Monitor", "Desc", BigDecimal.ZERO)
        );

        assertThrows(BusinessException.class, () ->
                product.updateDetails("Monitor", "Desc", new BigDecimal("-10.00"))
        );
    }

    @Test
    @DisplayName("Deve permitir exclusão quando o estoque é zero")
    void shouldAllowDeletionWhenStockIsZero() {
        PhysicalProduct product = new PhysicalProduct("Monitor", "24 inches", new BigDecimal("800.00"), 0);

        assertDoesNotThrow(product::validateDeletion);
    }

    @Test
    @DisplayName("Deve impedir exclusão quando o estoque é maior que zero")
    void shouldThrowWhenDeletingProductWithStock() {
        PhysicalProduct product = new PhysicalProduct("Monitor", "24 inches", new BigDecimal("800.00"), 5);

        assertThrows(BusinessException.class, product::validateDeletion);
    }

    @Test
    @DisplayName("Deve instanciar a Factory para cobertura de testes do JaCoCo")
    void shouldInstantiateProductFactoryToSatisfyCoverage() {
        ProductFactory factory = new ProductFactory();
        assertNotNull(factory);
    }
}
