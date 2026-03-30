package com.leonardomuniz.inventorysystem.integration;

import com.leonardomuniz.inventorysystem.model.Product;
import com.leonardomuniz.inventorysystem.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ProductHttpIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductRepository repository;

    @Test
    @DisplayName("Deve executar o fluxo completo de navegação: listagem, criação, erro de validação e tentativa de deleção")
    void testFullFlow() throws Exception {
        // 1. GET - Listagem inicial
        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/list"));

        // 2. POST - Criar Produto com sucesso
        mockMvc.perform(post("/products")
                        .param("name", "Integrated Product")
                        .param("description", "Full flow test")
                        .param("price", "50.0")
                        .param("quantity", "5")
                        .param("category", "PHYSICAL"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));

        // BUSCA O ID REAL: Evita falhas caso o banco não reinicie o contador de ID
        Product savedProduct = repository.findAll().stream()
                .filter(p -> p.getName().equals("Integrated Product"))
                .findFirst()
                .orElseThrow();
        Long dynamicId = savedProduct.getId();

        // 3. POST - Validar erro de formulário (nome vazio)
        mockMvc.perform(post("/products")
                        .param("name", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("products/form"))
                .andExpect(model().hasErrors());

        mockMvc.perform(post("/products/" + dynamicId + "/delete"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"))
                .andExpect(model().attribute("errorTitle", "Business Rule Violation"));
    }
}
