package com.leonardomuniz.inventorysystem.controller;

import com.leonardomuniz.inventorysystem.dto.ProductDTO;
import com.leonardomuniz.inventorysystem.model.Product;
import com.leonardomuniz.inventorysystem.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@Tag(name = "Product REST API", description = "Operações de manipulação de dados em formato JSON")
@RequiredArgsConstructor
public class ProductRestController {

    private final ProductService service;

    @Operation(summary = "Obter todos os produtos", description = "Retorna uma lista JSON com todos os produtos físicos e digitais")
    @GetMapping
    public ResponseEntity<List<Product>> getAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @Operation(summary = "Obter produto por ID", description = "Busca os detalhes de um produto específico através do seu identificador único")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDTO> getById(@PathVariable Long id) {
        Product product = service.findById(id);
        return ResponseEntity.ok(ProductDTO.fromEntity(product));
    }

    @Operation(summary = "Cadastrar novo produto", description = "Cria um novo registro de produto no sistema a partir dos dados fornecidos no corpo da requisição")
    @PostMapping
    public ResponseEntity<Product> create(@Valid @RequestBody ProductDTO dto) {
        Product created = service.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Atualizar produto existente", description = "Modifica os dados de um produto já cadastrado com base no ID informado")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        Product updated = service.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Remover produto", description = "Exclui permanentemente um produto do inventário")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
