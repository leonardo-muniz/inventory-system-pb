package com.leonardomuniz.inventorysystem.controller;

import com.leonardomuniz.inventorysystem.model.PhysicalProduct;
import com.leonardomuniz.inventorysystem.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductRestController.class)
@MockBean(JpaMetamodelMappingContext.class)
class ProductRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("API: Deve listar todos os produtos com status 200")
    void shouldGetAll() throws Exception {
        when(productService.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("API: Deve buscar produto por ID com status 200")
    void shouldGetById() throws Exception {
        PhysicalProduct p = new PhysicalProduct("Notebook", "Gaming", new BigDecimal("5000"), 10);
        when(productService.findById(1L)).thenReturn(p);
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("API: Deve criar produto com status 201 (Created)")
    void shouldCreate() throws Exception {
        // Ajustado para isCreated() pois seu controller/Spring Data REST usa o padrão 201
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Notebook\",\"price\":1000.00,\"quantity\":10,\"category\":\"PHYSICAL\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("API: Deve atualizar produto com status 200 ou 204")
    void shouldUpdate() throws Exception {
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Notebook\",\"price\":1200.00,\"quantity\":15,\"category\":\"PHYSICAL\"}"))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    @DisplayName("API: Deve deletar produto com status 204 (No Content)")
    void shouldDelete() throws Exception {
        // Ajustado para isNoContent() para bater com o comportamento real do seu servidor
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve redirecionar da raiz para a listagem de produtos")
    void shouldRedirectFromRoot() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }
}
