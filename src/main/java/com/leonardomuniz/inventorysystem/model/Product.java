package com.leonardomuniz.inventorysystem.model;

import com.leonardomuniz.inventorysystem.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "category_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Exigência do JPA, mas protegido para manter integridade
@EntityListeners(AuditingEntityListener.class)
public abstract class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    protected Product(String name, String description, BigDecimal price, Integer quantity) {
        validateCreationLimits(price, quantity);
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    @PrePersist
    protected void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    // Commands de Domínio (Substituem os Setters)
    public void updateDetails(String name, String description, BigDecimal price) {
        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Price must be greater than zero.");
        }
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public void updateStock(Integer newQuantity) {
        validateStockLimits(newQuantity);
        this.quantity = newQuantity;
    }

    public void validateDeletion() {
        if (this.quantity > 0) {
            throw new BusinessException("Cannot delete product with stock available");
        }
    }

    // Contratos Polimórficos
    public abstract String getCategoryType();
    protected abstract void validateStockLimits(Integer quantity);
    protected abstract void validateCreationLimits(BigDecimal price, Integer quantity);
}
