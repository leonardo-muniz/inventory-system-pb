package com.leonardomuniz.inventorysystem.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. RECURSO NÃO ENCONTRADO (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    public Object handleNotFound(ResourceNotFoundException ex, Model model, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return buildJsonResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
        }
        model.addAttribute("errorTitle", "Product Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("status", 404);
        return "error";
    }

    // 2. ERROS DE VALIDAÇÃO @Valid (400)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Object handleValidation(MethodArgumentNotValidException ex, Model model, HttpServletRequest request) {
        if (isApiRequest(request)) {
            // Extrai os nomes dos campos e os erros para o JSON
            String details = ex.getBindingResult().getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            return buildJsonResponse(HttpStatus.BAD_REQUEST, "Validation failed: " + details, request);
        }
        model.addAttribute("errorTitle", "Invalid Input");
        model.addAttribute("errorMessage", "Please check the form fields and try again.");
        model.addAttribute("status", 400);
        return "error";
    }

    // 3. VIOLAÇÃO DE REGRAS DE NEGÓCIO (422)
    @ExceptionHandler(BusinessException.class)
    public Object handleBusiness(BusinessException ex, Model model, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return buildJsonResponse(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage(), request);
        }
        model.addAttribute("errorTitle", "Business Rule Violation");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("status", 422);
        return "error";
    }

    // 4. ERROS DE BANCO DE DADOS / INTEGRIDADE (409)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public Object handleDatabase(DataIntegrityViolationException ex, Model model, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return buildJsonResponse(HttpStatus.CONFLICT, "Database integrity error (possible duplicate record).", request);
        }
        model.addAttribute("errorTitle", "Database Error");
        model.addAttribute("errorMessage", "A database integrity error occurred.");
        model.addAttribute("status", 409);
        return "error";
    }

    // 5. ERRO GENÉRICO (500)
    @ExceptionHandler(Exception.class)
    public Object handleGeneric(Exception ex, Model model, HttpServletRequest request) {
        if (isApiRequest(request)) {
            return buildJsonResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected internal error occurred.", request);
        }
        model.addAttribute("errorTitle", "Unexpected Error");
        model.addAttribute("errorMessage", "Something went wrong. Please try again later.");
        model.addAttribute("status", 500);
        return "error";
    }

    // --- MÉTODOS AUXILIARES ---

    private boolean isApiRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api");
    }

    private ResponseEntity<ErrorResponse> buildJsonResponse(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
}