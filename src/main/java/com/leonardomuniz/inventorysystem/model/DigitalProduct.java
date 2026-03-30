package com.leonardomuniz.inventorysystem.model;

import com.leonardomuniz.inventorysystem.exception.BusinessException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("DIGITAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DigitalProduct extends Product {

    public DigitalProduct(String name, String description, BigDecimal price) {
        super(name, description, price, 0); // Produto digital não tem estoque físico
    }

    @Override
    public String getCategoryType() {
        return "DIGITAL";
    }

    @Override
    protected void validateStockLimits(Integer quantity) {
        if (quantity > 0) {
            throw new BusinessException("Digital products cannot have physical stock");
        }
    }

    @Override
    protected void validateCreationLimits(BigDecimal price, Integer quantity) {
        if (price.doubleValue() > 500_000) {
            throw new BusinessException("Price exceeds allowed limit for digital products");
        }
    }
}
