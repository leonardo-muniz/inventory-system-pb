package com.leonardomuniz.inventorysystem.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.leonardomuniz.inventorysystem.dto.ProductDTO;
import com.leonardomuniz.inventorysystem.exception.BusinessException;
import com.leonardomuniz.inventorysystem.exception.GlobalExceptionHandler;
import com.leonardomuniz.inventorysystem.exception.ResourceNotFoundException;
import com.leonardomuniz.inventorysystem.model.PhysicalProduct;
import com.leonardomuniz.inventorysystem.model.Product;
import com.leonardomuniz.inventorysystem.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Collections;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
@MockBean(JpaMetamodelMappingContext.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Deve retornar a view de listagem com a lista de produtos")
    void shouldReturnProductsListView() throws Exception {
        when(productService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    @DisplayName("Deve retornar a view do formulário para criação de novo produto")
    void shouldReturnProductsFormViewForCreate() throws Exception {
        mockMvc.perform(get("/products/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeExists("productDTO"))
                .andExpect(model().attribute("productId", 0L));
    }

    @Test
    @DisplayName("Deve criar um produto e redirecionar para a listagem com sucesso")
    void shouldCreateProductAndRedirect() throws Exception {
        mockMvc.perform(post("/products")
                        .param("name", "Notebook")
                        .param("description", "Test Notebook")
                        .param("price", "3500.00")
                        .param("quantity", "10")
                        .param("category", "PHYSICAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(productService, times(1)).create(any(ProductDTO.class));
    }

    @Test
    @DisplayName("Deve retornar ao formulário quando houver erro de validação na criação")
    void shouldReturnFormWhenValidationFailsOnCreate() throws Exception {
        mockMvc.perform(post("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Deve exibir view de erro quando ocorrer BusinessException na criação")
    void shouldReturnErrorViewWhenBusinessExceptionOnCreate() throws Exception {
        doThrow(new BusinessException("Business rule error")).when(productService).create(any(ProductDTO.class));

        mockMvc.perform(post("/products")
                        .param("name", "Notebook")
                        .param("description", "Test Notebook")
                        .param("price", "3500.00")
                        .param("quantity", "10")
                        .param("category", "PHYSICAL"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Business Rule Violation"));
    }

    @Test
    @DisplayName("Deve carregar o formulário de edição com os dados do produto encontrado")
    void shouldReturnProductsFormViewWhenProductIsFound() throws Exception {
        Product mockProduct = new PhysicalProduct(
                "Notebook",
                "Test Notebook",
                new BigDecimal("3500.00"),
                10
        );
        ReflectionTestUtils.setField(mockProduct, "id", 1L);

        when(productService.findById(1L)).thenReturn(mockProduct);

        mockMvc.perform(get("/products/1/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().attributeExists("productDTO"))
                .andExpect(model().attribute("productId", 1L));
    }

    @Test
    @DisplayName("Deve exibir view de erro 404 quando o produto não for encontrado para edição")
    void shouldReturnErrorViewWhenProductNotFound() throws Exception {
        when(productService.findById(99L)).thenThrow(new ResourceNotFoundException("Product not found"));

        mockMvc.perform(get("/products/99/edit"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Product Not Found"));
    }

    @Test
    @DisplayName("Deve atualizar o produto e redirecionar para a listagem")
    void shouldUpdateProductAndRedirect() throws Exception {
        mockMvc.perform(post("/products/1")
                        .param("name", "Updated Notebook")
                        .param("description", "Updated Description")
                        .param("price", "4000.00")
                        .param("quantity", "15")
                        .param("category", "PHYSICAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(productService, times(1)).update(eq(1L), any(ProductDTO.class));
    }

    @Test
    @DisplayName("Deve exibir view de erro quando houver violação de regra de negócio na atualização")
    void shouldReturnErrorViewWhenBusinessExceptionOnUpdate() throws Exception {
        doThrow(new BusinessException("Business rule error")).when(productService).update(eq(1L), any(ProductDTO.class));

        mockMvc.perform(post("/products/1")
                        .param("name", "Notebook")
                        .param("price", "3500.00")
                        .param("quantity", "10")
                        .param("category", "PHYSICAL"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attributeExists("errorTitle"));
    }

    @Test
    @DisplayName("Deve deletar o produto e redirecionar com mensagem de sucesso")
    void shouldDeleteProductAndRedirect() throws Exception {
        mockMvc.perform(post("/products/1/delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"))
                .andExpect(flash().attributeExists("successMessage"));

        verify(productService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Deve retornar formulário quando atualização falha na validação")
    void shouldReturnFormOnUpdateValidationError() throws Exception {
        mockMvc.perform(post("/products/1")
                        .param("name", "")) // Nome vazio para forçar erro
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().hasErrors());
    }
}
