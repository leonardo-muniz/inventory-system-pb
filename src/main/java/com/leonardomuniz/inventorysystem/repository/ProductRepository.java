package com.leonardomuniz.inventorysystem.repository;

import com.leonardomuniz.inventorysystem.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
}
