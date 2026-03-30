package com.leonardomuniz.inventorysystem.service;

import com.leonardomuniz.inventorysystem.dto.ProductDTO;
import com.leonardomuniz.inventorysystem.exception.BusinessException;
import com.leonardomuniz.inventorysystem.exception.ResourceNotFoundException;
import com.leonardomuniz.inventorysystem.model.Product;
import com.leonardomuniz.inventorysystem.model.ProductFactory;
import com.leonardomuniz.inventorysystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;

    public Product create(ProductDTO dto) {
        if (repository.existsByNameIgnoreCase(dto.name())) {
            throw new BusinessException("Product with this name already exists");
        }

        Product product = ProductFactory.create(
                dto.category(), dto.name(), dto.description(), dto.price(), dto.quantity()
        );

        return repository.save(product);
    }

    public List<Product> findAll() {
        return repository.findAll();
    }

    public Product findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    public Product update(Long id, ProductDTO dto) {
        Product existing = findById(id);

        if (!existing.getName().equalsIgnoreCase(dto.name()) && repository.existsByNameIgnoreCase(dto.name())) {
            throw new BusinessException("Another product with this name already exists");
        }

        existing.updateDetails(dto.name(), dto.description(), dto.price());
        existing.updateStock(dto.quantity());

        return repository.save(existing);
    }

    public void delete(Long id) {
        Product product = findById(id);
        product.validateDeletion();
        repository.delete(product);
    }
}
