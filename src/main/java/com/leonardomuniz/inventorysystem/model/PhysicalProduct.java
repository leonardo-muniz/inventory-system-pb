package com.leonardomuniz.inventorysystem.model;

import com.leonardomuniz.inventorysystem.exception.BusinessException;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("PHYSICAL")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PhysicalProduct extends Product {

    public PhysicalProduct(String name, String description, BigDecimal price, Integer quantity) {
        super(name, description, price, quantity);
    }

    @Override
    public String getCategoryType() {
        return "PHYSICAL";
    }

    @Override
    protected void validateStockLimits(Integer quantity) {
        if (quantity > 10_000) {
            throw new BusinessException("Quantity exceeds physical stock policy limit");
        }
    }

    @Override
    protected void validateCreationLimits(BigDecimal price, Integer quantity) {
        if (price.doubleValue() > 1_000_000) {
            throw new BusinessException("Price exceeds allowed limit for physical products");
        }
    }
}
