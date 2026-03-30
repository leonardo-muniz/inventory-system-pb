package com.leonardomuniz.inventorysystem.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerApiTest {

    private GlobalExceptionHandler handler;
    private MockHttpServletRequest request;
    private ExtendedModelMap model;

    @BeforeEach
    void setUp() {
        handler = new GlobalExceptionHandler();
        request = new MockHttpServletRequest();
        model = new ExtendedModelMap();
    }

    @Test
    @DisplayName("Deve retornar JSON quando a requisição for de API (/api/...)")
    void shouldReturnResponseEntityWhenApiRequest() {
        // Simula um caminho de API
        request.setRequestURI("/api/products");
        ResourceNotFoundException ex = new ResourceNotFoundException("Not found");

        Object result = handler.handleNotFound(ex, model, request);

        // Verifica se o retorno é ResponseEntity (Fluxo de API) e não String (Fluxo Web)
        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    @DisplayName("Deve cobrir a classe ErrorResponse")
    void shouldCoverErrorResponse() {
        // Este teste serve para tirar o ErrorResponse do 0%
        request.setRequestURI("/api/test");
        BusinessException ex = new BusinessException("Error");

        ResponseEntity<?> result = (ResponseEntity<?>) handler.handleBusiness(ex, model, request);
        ErrorResponse error = (ErrorResponse) result.getBody();

        assertNotNull(error);
        assertEquals(422, error.status());
        assertNotNull(error.timestamp());
        assertEquals("/api/test", error.path());
    }

    @Test
    @DisplayName("Handler: Deve testar ramificação de API para todos os métodos")
    void testApiBranches() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");
        ExtendedModelMap model = new ExtendedModelMap();

        // Cobre handleNotFound ramificação API
        assertInstanceOf(ResponseEntity.class, handler.handleNotFound(new ResourceNotFoundException(""), model, request));
        // Cobre handleBusiness ramificação API
        assertInstanceOf(ResponseEntity.class, handler.handleBusiness(new BusinessException(""), model, request));
        // Cobre handleGeneric ramificação API
        assertInstanceOf(ResponseEntity.class, handler.handleGeneric(new Exception(""), model, request));
    }

    @Test
    @DisplayName("Handler: Deve cobrir a Lambda de erros de validação")
    void testValidationLambda() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("/api/test");

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(new Object(), "obj");
        result.addError(new FieldError("obj", "field", "error message"));
        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, result);

        ResponseEntity<?> response = (ResponseEntity<?>) handler.handleValidation(ex, new ExtendedModelMap(), request);
        assertNotNull(response.getBody());
    }

    @Test
    @DisplayName("Deve retornar JSON em handleDatabase quando a requisição for de API")
    void shouldReturnJsonInHandleDatabaseWhenApiRequest() {
        // Arrange
        MockHttpServletRequest apiRequest = new MockHttpServletRequest();
        apiRequest.setRequestURI("/api/products");

        DataIntegrityViolationException ex = new DataIntegrityViolationException("Duplicate key");

        // Act
        Object result = handler.handleDatabase(ex, model, apiRequest);

        // Assert
        assertInstanceOf(ResponseEntity.class, result);
        ResponseEntity<?> response = (ResponseEntity<?>) result;
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
    }
}
