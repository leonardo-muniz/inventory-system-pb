package com.leonardomuniz.inventorysystem.exception;

import org.springframework.mock.web.MockHttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;
    private Model model;
    private MockHttpServletRequest request;

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        model = new ExtendedModelMap();
        request = new MockHttpServletRequest();
    }

    @Test
    @DisplayName("Deve manipular ResourceNotFoundException e preencher o Model com status 404")
    void shouldHandleResourceNotFoundException() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found message");
        Object result = exceptionHandler.handleNotFound(ex, model, request);

        assertNotNull(result);
        assertEquals("Product Not Found", model.getAttribute("errorTitle"));
        assertEquals(404, model.getAttribute("status"));
    }

    @Test
    @DisplayName("Deve manipular BusinessException e preencher o Model com status 422")
    void shouldHandleBusinessException() {
        BusinessException ex = new BusinessException("Business error message");
        Object result = exceptionHandler.handleBusiness(ex, model, request);

        assertNotNull(result);
        assertEquals("Business Rule Violation", model.getAttribute("errorTitle"));
        assertEquals(422, model.getAttribute("status"));
    }

    @Test
    @DisplayName("Deve manipular erros de validação (MethodArgumentNotValidException) com status 400")
    void shouldHandleMethodArgumentNotValidException() {
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null,
                new org.springframework.validation.BeanPropertyBindingResult(null, "null"));

        Object result = exceptionHandler.handleValidation(ex, model, request);

        assertNotNull(result);
        assertEquals("Invalid Input", model.getAttribute("errorTitle"));
        assertEquals(400, model.getAttribute("status"));
    }

    @Test
    @DisplayName("Deve manipular violação de integridade de dados (DataIntegrityViolationException) com status 409")
    void shouldHandleDataIntegrityViolationException() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("DB error");
        Object result = exceptionHandler.handleDatabase(ex, model, request);

        assertNotNull(result);
        assertEquals("Database Error", model.getAttribute("errorTitle"));
        assertEquals(409, model.getAttribute("status"));
    }

    @Test
    @DisplayName("Deve manipular exceções genéricas e retornar status 500")
    void shouldHandleGenericException() {
        Exception ex = new Exception("Some random generic error");
        Object result = exceptionHandler.handleGeneric(ex, model, request);

        assertNotNull(result);
        assertEquals("Unexpected Error", model.getAttribute("errorTitle"));
        assertEquals(500, model.getAttribute("status"));
    }
}
