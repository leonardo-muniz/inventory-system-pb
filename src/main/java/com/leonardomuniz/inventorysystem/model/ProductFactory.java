package com.leonardomuniz.inventorysystem.model;

import java.math.BigDecimal;

public class ProductFactory {
    public static Product create(String category, String name, String description, BigDecimal price, Integer quantity) {
        if ("DIGITAL".equalsIgnoreCase(category)) {
            return new DigitalProduct(name, description, price);
        }
        return new PhysicalProduct(name, description, price, quantity);
    }
}
