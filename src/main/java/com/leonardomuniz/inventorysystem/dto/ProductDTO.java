package com.leonardomuniz.inventorysystem.dto;

import com.leonardomuniz.inventorysystem.model.Product;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductDTO(
        Long id,
        @NotBlank(message = "Name is required") @Size(min = 3, max = 100) String name,
        @Size(max = 255, message = "Description must be at most 255 characters") String description,
        @NotNull @DecimalMin(value = "0.01", message = "Price must be greater than zero") BigDecimal price,
        @NotNull @Min(value = 0, message = "Quantity cannot be negative") Integer quantity,
        @NotBlank String category,
        LocalDateTime updatedAt
) {
    public static ProductDTO fromEntity(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getQuantity(),
                product.getCategoryType(),
                product.getUpdatedAt()
        );
    }
}
